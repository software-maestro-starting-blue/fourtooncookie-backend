package com.startingblue.fourtooncookie.character.exception;

import java.util.NoSuchElementException;

public final class CharacterNoSuchElementException extends NoSuchElementException {
    public CharacterNoSuchElementException() {
        super("The character is empty");
    }
}
