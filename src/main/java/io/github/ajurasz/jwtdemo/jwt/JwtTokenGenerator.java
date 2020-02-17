package io.github.ajurasz.jwtdemo.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
class JwtTokenGenerator implements TokenGenerator {
    private final Duration expirationTime;
    private final byte[] secret;

    JwtTokenGenerator(@Value("${token.expirationTime}") Duration expirationTime,
                      @Value("${token.secret}") String secret) {
        this.expirationTime = expirationTime;
        this.secret = secret.getBytes();
    }

    @Override
    public String generateFor(String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(expireAt())
                .withIssuedAt(issuedAt())
                .sign(Algorithm.HMAC512(secret));
    }

    private Date issuedAt() {
        return Date.from(Instant.now());
    }

    private Date expireAt() {
        return Date.from(Instant.now().plusSeconds(expirationTime.toSeconds()));
    }
}
