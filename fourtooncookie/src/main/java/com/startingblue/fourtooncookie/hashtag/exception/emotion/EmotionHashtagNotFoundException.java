package com.startingblue.fourtooncookie.hashtag.exception.emotion;

import com.startingblue.fourtooncookie.hashtag.exception.common.HashtagNotFoundException;

public class EmotionHashtagNotFoundException extends HashtagNotFoundException {

    public EmotionHashtagNotFoundException() {
        super("emotion hashtag Not Found");
    }

    public EmotionHashtagNotFoundException(String message) {
        super(message);
    }

    public EmotionHashtagNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
