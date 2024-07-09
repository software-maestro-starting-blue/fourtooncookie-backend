package com.startingblue.fourtooncookie.hashtag.exception.common;

public class HashtagExistsException extends RuntimeException {

    public HashtagExistsException() {
        super("Hashtag already exists");
    }

    public HashtagExistsException(String message) {
        super(message);
    }

    public HashtagExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
