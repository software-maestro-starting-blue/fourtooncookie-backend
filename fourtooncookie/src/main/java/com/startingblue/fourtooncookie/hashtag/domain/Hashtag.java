package com.startingblue.fourtooncookie.hashtag.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@Getter
@AllArgsConstructor
public enum Hashtag {

    SAD(1L, "슬픔", HashtagType.EMOTION),
    DSAS(2L, "슬픔", HashtagType.EMOTION);
    // TODO : 해시 태그 종류 및 아이디 관리는 회의 필요

    private final Long id;
    private final String name;
    private final HashtagType hashtagType;

    public static List<Hashtag> findHashtagsByIds(List<Long> ids) {
        List<Hashtag> result = new ArrayList<>();
        for (Long id : ids) {
            Hashtag hashtag = findHashtagById(id);
            result.add(hashtag);
        }
        return result;
    }

    private static Hashtag findHashtagById(Long id) {
        for (Hashtag hashtag : Hashtag.values()) {
            if (hashtag.getId().equals(id)) {
                return hashtag;
            }
        }
        throw new IllegalArgumentException("Hashtag with id " + id + " does not exist");
    }
}
