package com.startingblue.fourtooncookie.member.authorization.exception;

import java.util.NoSuchElementException;

public class TokenNotFoundException extends NoSuchElementException {

    public TokenNotFoundException(String message) {
        super(message);
    }
}