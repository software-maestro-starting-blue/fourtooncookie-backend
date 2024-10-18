package com.startingblue.fourtooncookie.character.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class CharacterNotFoundException extends NoSuchElementException {

    public CharacterNotFoundException(String message) {
        super(message);
    }
}
