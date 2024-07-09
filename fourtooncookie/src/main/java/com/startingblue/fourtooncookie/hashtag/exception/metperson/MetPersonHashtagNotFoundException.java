package com.startingblue.fourtooncookie.hashtag.exception.metperson;

import com.startingblue.fourtooncookie.hashtag.exception.common.HashtagNotFoundException;

public class MetPersonHashtagNotFoundException extends HashtagNotFoundException {

    public MetPersonHashtagNotFoundException() {
        super("metPerson hashtag Not Found");
    }

    public MetPersonHashtagNotFoundException(String message) {
        super(message);
    }

    public MetPersonHashtagNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
