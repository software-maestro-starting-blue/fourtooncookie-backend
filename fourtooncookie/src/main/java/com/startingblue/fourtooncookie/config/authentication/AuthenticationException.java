package com.startingblue.fourtooncookie.config.authentication;

import lombok.Getter;

@Getter
public final class AuthenticationException extends RuntimeException {
    private final int statusCode;

    public AuthenticationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public AuthenticationException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}