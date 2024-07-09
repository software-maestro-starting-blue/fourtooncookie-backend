package com.startingblue.fourtooncookie.hashtag.domain.emotion;

import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.hashtag.domain.HashtagFactory;

public class EmotionHashtagFactory extends HashtagFactory {

    @Override
    protected Hashtag createHashtag(String name) {
        return new EmotionHashtag(name);
    }
}
