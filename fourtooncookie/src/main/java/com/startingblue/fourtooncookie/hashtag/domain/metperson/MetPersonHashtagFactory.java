package com.startingblue.fourtooncookie.hashtag.domain.metperson;

import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.hashtag.domain.HashtagFactory;

public class MetPersonHashtagFactory extends HashtagFactory {

    @Override
    protected Hashtag createHashtag(String name) {
        return new MetPersonHashtag(name);
    }
}
