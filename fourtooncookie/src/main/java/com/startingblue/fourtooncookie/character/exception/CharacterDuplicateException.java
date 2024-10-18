package com.startingblue.fourtooncookie.character.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CharacterDuplicateException extends RuntimeException {
    public CharacterDuplicateException(String message) {
        super(message);
    }
}
