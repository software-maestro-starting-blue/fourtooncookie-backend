package com.startingblue.fourtooncookie.hashtag.exception.common;

public class HashtagNotFoundException extends RuntimeException {

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
