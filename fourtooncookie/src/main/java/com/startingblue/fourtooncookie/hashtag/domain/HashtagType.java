package com.startingblue.fourtooncookie.hashtag.domain;

import com.startingblue.fourtooncookie.hashtag.exception.HashtagIdInvalidException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HashtagType {

    WEATHER(1L,"날씨", 1L, 99L),
    EMOTION(2L,"감정", 100L, 199L),
    MET_PERSON(3L,"만난 사람", 200L, 299L);

    private final Long id;
    private final String name;
    private final Long minHashtagId;
    private final Long maxHashtagId;

    public void validateHashtagId(Long hashtagId) {
        if (hashtagId < minHashtagId || hashtagId > maxHashtagId) {
            throw new HashtagIdInvalidException(name + " hashtags should have IDs between " + minHashtagId + " and " + maxHashtagId);
        }
    }
}
