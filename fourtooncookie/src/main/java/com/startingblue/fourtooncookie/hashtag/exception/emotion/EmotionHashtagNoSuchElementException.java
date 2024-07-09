package com.startingblue.fourtooncookie.hashtag.exception.emotion;

import com.startingblue.fourtooncookie.hashtag.exception.common.HashtagNoSuchElementException;

public class EmotionHashtagNoSuchElementException extends HashtagNoSuchElementException {

    public EmotionHashtagNoSuchElementException() {
        super("emotion hashtag Not Found");
    }

    public EmotionHashtagNoSuchElementException(String message) {
        super(message);
    }

    public EmotionHashtagNoSuchElementException(String message, Throwable cause) {
        super(message, cause);
    }
}
