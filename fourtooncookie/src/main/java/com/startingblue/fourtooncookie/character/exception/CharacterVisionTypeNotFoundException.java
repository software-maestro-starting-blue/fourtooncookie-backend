package com.startingblue.fourtooncookie.character.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class CharacterVisionTypeNotFoundException extends NoSuchElementException {
    public CharacterVisionTypeNotFoundException(String message) {
        super("Invalid CharacterVisionType value: " + message);
    }
}
