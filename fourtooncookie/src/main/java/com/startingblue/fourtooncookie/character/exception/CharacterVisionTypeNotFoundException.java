package com.startingblue.fourtooncookie.character.exception;

import java.util.NoSuchElementException;

public final class CharacterVisionTypeNotFoundException extends NoSuchElementException {
    public CharacterVisionTypeNotFoundException(String message) {
        super("Invalid CharacterVisionType value: " + message);
    }
}
