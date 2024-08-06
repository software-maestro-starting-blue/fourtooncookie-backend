package com.startingblue.fourtooncookie.hashtag.exception;

public class HashtagNotFoundException extends RuntimeException {

    public HashtagNotFoundException() {
        super("Hashtag not found");
    }
}
