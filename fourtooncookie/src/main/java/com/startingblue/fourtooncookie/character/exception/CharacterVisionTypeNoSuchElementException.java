package com.startingblue.fourtooncookie.character.exception;

import java.util.NoSuchElementException;

public final class CharacterVisionTypeNoSuchElementException extends NoSuchElementException {
    public CharacterVisionTypeNoSuchElementException() {
        super("The model type is empty");
    }
}
