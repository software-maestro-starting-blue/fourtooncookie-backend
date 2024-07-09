package com.startingblue.fourtooncookie.hashtag.exception.common;

import java.util.NoSuchElementException;

public class HashtagNoSuchElementException extends NoSuchElementException {

    public HashtagNoSuchElementException() {
        super("Hashtag not found");
    }

    public HashtagNoSuchElementException(String message) {
        super(message);
    }

    public HashtagNoSuchElementException(String message, Throwable cause) {
        super(message, cause);
    }
}
