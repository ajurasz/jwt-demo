package io.github.ajurasz.jwtdemo.jwt;

public interface TokenGenerator {
    String generateFor(String username);
}
