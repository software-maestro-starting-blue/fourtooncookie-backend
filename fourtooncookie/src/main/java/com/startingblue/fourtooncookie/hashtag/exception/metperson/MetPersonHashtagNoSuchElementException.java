package com.startingblue.fourtooncookie.hashtag.exception.metperson;

import com.startingblue.fourtooncookie.hashtag.exception.common.HashtagNoSuchElementException;

public class MetPersonHashtagNoSuchElementException extends HashtagNoSuchElementException {

    public MetPersonHashtagNoSuchElementException() {
        super("metPerson hashtag Not Found");
    }

    public MetPersonHashtagNoSuchElementException(String message) {
        super(message);
    }

    public MetPersonHashtagNoSuchElementException(String message, Throwable cause) {
        super(message, cause);
    }
}
