package com.startingblue.fourtooncookie.hashtag.exception;

import java.util.NoSuchElementException;

public class HashtagNotFoundException extends NoSuchElementException {

    public HashtagNotFoundException() {
        super("Hashtag not found");
    }

    public HashtagNotFoundException(String message) {
        super(message);
    }

    public HashtagNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
