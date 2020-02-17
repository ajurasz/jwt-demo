package io.github.ajurasz.jwtdemo.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public static final String AUTH_URL = "/api/authenticate";
    private final ObjectMapper objectMapper;
    private final TokenGenerator tokenGenerator;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   ObjectMapper objectMapper,
                                   TokenGenerator tokenGenerator) {
        super(new AntPathRequestMatcher(AUTH_URL, "POST"));
        setAuthenticationManager(authenticationManager);
        this.objectMapper = objectMapper;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        return extractCredentials(request)
                .map(this::authenticate)
                .orElseThrow(AuthenticationFailure::new);
    }

    private Optional<UserCredentials> extractCredentials(HttpServletRequest request) {
        try {
            return Optional.of(objectMapper.readValue(request.getInputStream(), UserCredentials.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private Authentication authenticate(UserCredentials credentials) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(credentials.username, credentials.password, List.of());
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        String token = tokenGenerator.generateFor(authResult.getName());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("token", token)));
    }

    static class AuthenticationFailure extends RuntimeException {
        AuthenticationFailure() {
            super("Authentication process failed");
        }
    }

    private static class UserCredentials {
        private final String username;
        private final String password;

        UserCredentials(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
