package io.github.ajurasz.jwtdemo.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class JwtVerificationFilter extends OncePerRequestFilter {
    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final int TOKEN_START_INDEX = 7;
    private final TokenReader tokenReader;

    public JwtVerificationFilter(TokenReader tokenReader) {
        this.tokenReader = tokenReader;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        authorizationHeader(request)
                .flatMap(this::extractToken)
                .flatMap(this::parseToken)
                .ifPresentOrElse(this::setSecurityContext, SecurityContextHolder::clearContext);

        filterChain.doFilter(request, response);
    }

    private Optional<String> authorizationHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTH_HEADER));
    }

    private Optional<String> extractToken(String authorizationHeaderValue) {
        return Optional.of(authorizationHeaderValue)
                .filter(Predicate.not(String::isEmpty))
                .filter(value -> value.startsWith(TOKEN_PREFIX))
                .map(value -> value.substring(TOKEN_START_INDEX));
    }

    private Optional<Token> parseToken(String token) {
        try {
            return Optional.of(tokenReader.read(token));
        } catch (InvalidToken invalidToken) {
            return Optional.empty();
        }
    }

    private void setSecurityContext(Token token) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                token.username(),
                token.token(),
                List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
