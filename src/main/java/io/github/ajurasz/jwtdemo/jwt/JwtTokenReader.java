package io.github.ajurasz.jwtdemo.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Service
class JwtTokenReader implements TokenReader {
    private final byte[] secret;

    public JwtTokenReader(@Value("${token.secret}") String secret) {
        this.secret = secret.getBytes();
    }

    @Override
    public Token read(String token) {
        try {
            DecodedJWT jwt = JWT.require(HMAC512(secret))
                    .build()
                    .verify(token);
            return Token.from(jwt.getSubject(), jwt.getToken());
        } catch (JWTVerificationException ex) {
            throw new InvalidToken(ex);
        }
    }
}
