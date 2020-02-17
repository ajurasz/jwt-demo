package io.github.ajurasz.jwtdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ajurasz.jwtdemo.jwt.JwtAuthenticationFilter;
import io.github.ajurasz.jwtdemo.jwt.JwtVerificationFilter;
import io.github.ajurasz.jwtdemo.jwt.TokenGenerator;
import io.github.ajurasz.jwtdemo.jwt.TokenReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final ObjectMapper objectMapper;
    private final TokenGenerator tokenGenerator;
    private final TokenReader tokenReader;

    SecurityConfiguration(ObjectMapper objectMapper, TokenGenerator tokenGenerator, TokenReader tokenReader) {
        this.objectMapper = objectMapper;
        this.tokenGenerator = tokenGenerator;
        this.tokenReader = tokenReader;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password("{noop}admin123").roles("ADMIN")
                .and()
                .withUser("user").password("{noop}user123").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(), objectMapper, tokenGenerator),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtVerificationFilter(tokenReader), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api/**").authenticated();
    }
}
