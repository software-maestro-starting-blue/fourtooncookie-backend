package com.startingblue.fourtooncookie.hashtag.domain;

import com.startingblue.fourtooncookie.hashtag.domain.emotion.EmotionHashtagFactory;
import com.startingblue.fourtooncookie.hashtag.domain.metperson.MetPersonHashtagFactory;
import com.startingblue.fourtooncookie.hashtag.domain.weather.WeatherHashtagFactory;
import com.startingblue.fourtooncookie.hashtag.exception.common.HashtagTypeNotFoundException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class HashtagFactory {

    private static final Map<HashtagType, HashtagFactory> factories = new ConcurrentHashMap<>();

    static {
        factories.put(HashtagType.EMOTION, new EmotionHashtagFactory());
        factories.put(HashtagType.WEATHER, new WeatherHashtagFactory());
        factories.put(HashtagType.MET_PERSON, new MetPersonHashtagFactory());
    }

    public static Hashtag create(String name, HashtagType type) {
        HashtagFactory factory = factories.get(type);
        if (factory == null) {
            throw new HashtagTypeNotFoundException("Hashtag type " + type + " is not supported");
        }
        return factory.createHashtag(name);
    }

//    public static void registerHashtagType(HashtagType type, HashtagFactory factory) {
//        factories.put(type, factory);
//    }

    protected abstract Hashtag createHashtag(String name);
}
