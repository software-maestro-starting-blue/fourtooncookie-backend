package com.startingblue.fourtooncookie.hashtag.exception;

public class HashtagDuplicateException extends RuntimeException {

    public HashtagDuplicateException() {
        super("Hashtag is duplicated.");
    }

    public HashtagDuplicateException(String message) {
        super(message);
    }

    public HashtagDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}

