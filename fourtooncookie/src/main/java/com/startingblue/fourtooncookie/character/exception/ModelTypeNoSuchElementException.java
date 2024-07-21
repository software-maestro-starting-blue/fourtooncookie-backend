package com.startingblue.fourtooncookie.character.exception;

import java.util.NoSuchElementException;

public final class ModelTypeNoSuchElementException extends NoSuchElementException {
    public ModelTypeNoSuchElementException() {
        super("The model type is empty");
    }
}
