package com.startingblue.fourtooncookie.config.authentication;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public final class AuthenticationException extends RuntimeException {

    private final HttpStatus statusCode = HttpStatus.UNAUTHORIZED;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}