package com.startingblue.fourtooncookie.hashtag.exception;

public class HashtagTypeNotFoundException extends RuntimeException {

    public HashtagTypeNotFoundException() {
        super("hashtag type not found");
    }

    public HashtagTypeNotFoundException(String message) {
        super(message);
    }

    public HashtagTypeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
