package com.startingblue.fourtooncookie.hashtag.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashtagTypeTest {

    @Test
    @DisplayName("HashtagType의 ID와 이름이 올바르게 설정되었는지 확인한다.")
    void hashtagTypeValues_shouldHaveCorrectIdAndName() {
        assertEquals(1L, HashtagType.WEATHER.getId());
        assertEquals("날씨", HashtagType.WEATHER.getName());

        assertEquals(2L, HashtagType.EMOTION.getId());
        assertEquals("감정", HashtagType.EMOTION.getName());

        assertEquals(3L, HashtagType.MET_PERSON.getId());
        assertEquals("만난 사람", HashtagType.MET_PERSON.getName());
    }
}
