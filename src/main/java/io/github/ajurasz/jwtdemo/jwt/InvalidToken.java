package io.github.ajurasz.jwtdemo.jwt;

class InvalidToken extends RuntimeException {
    InvalidToken(Throwable cause) {
        super(cause);
    }
}
