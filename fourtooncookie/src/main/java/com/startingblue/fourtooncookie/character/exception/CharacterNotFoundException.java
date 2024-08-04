package com.startingblue.fourtooncookie.character.exception;

import java.util.NoSuchElementException;

public final class CharacterNotFoundException extends NoSuchElementException {
    public CharacterNotFoundException() {
        super("The character is empty");
    }
}
