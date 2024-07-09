package com.startingblue.fourtooncookie.hashtag.domain.weather;

import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.hashtag.domain.HashtagFactory;

public class WeatherHashtagFactory extends HashtagFactory {

    @Override
    protected Hashtag createHashtag(String name) {
        return new WeatherHashtag(name);
    }
}
