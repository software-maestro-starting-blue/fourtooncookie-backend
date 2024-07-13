package com.startingblue.fourtooncookie.hashtag.domain;

import com.startingblue.fourtooncookie.hashtag.exception.common.HashtagNoSuchElementException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum HashtagType {

    WEATHER(1L, "날씨"),
    EMOTION(2L, "감정"),
    MET_PERSON(3L, "만난 사람");

    private final Long id;

    private final String name;

    HashtagType(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static HashtagType from(String text) {
        for (HashtagType hashtagType : HashtagType.values()) {
            if (text.equalsIgnoreCase(hashtagType.name())) {
                return hashtagType;
            }
        }

        throw new HashtagNoSuchElementException();
    }
}
