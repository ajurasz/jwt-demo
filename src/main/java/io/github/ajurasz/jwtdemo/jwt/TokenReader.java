package io.github.ajurasz.jwtdemo.jwt;

public interface TokenReader {
    Token read(String token);
}
