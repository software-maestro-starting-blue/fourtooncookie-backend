package com.startingblue.fourtooncookie.hashtag.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum HashtagType {

    WEATHER(1L, "날씨"),
    EMOTION(2L, "감정"),
    MET_PERSON(3L, "만난 사람");

    private final Long id;

    private final String name;

}
