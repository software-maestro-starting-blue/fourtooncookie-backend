package com.startingblue.fourtooncookie.diary.exception;

import java.util.NoSuchElementException;

public class DiaryNoSuchElementException extends NoSuchElementException {

    public DiaryNoSuchElementException() {
        super();
    }

    public DiaryNoSuchElementException(String message) {
        super(message);
    }

    public DiaryNoSuchElementException(String message, Throwable cause) {
        super(message, cause);
    }
}
