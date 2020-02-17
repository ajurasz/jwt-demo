package io.github.ajurasz.jwtdemo.jwt;

class Token {
    private final String username;
    private final String token;

    private Token(String username, String token) {
        this.username = username;
        this.token = token;
    }

    static Token from(String username, String token) {
        return new Token(username, token);
    }

    String username() {
        return username;
    }

    String token() {
        return token;
    }
}
