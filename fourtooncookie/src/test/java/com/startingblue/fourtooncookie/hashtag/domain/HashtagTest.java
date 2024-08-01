package com.startingblue.fourtooncookie.hashtag.domain;

import com.startingblue.fourtooncookie.hashtag.exception.HashtagNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class HashtagTest {

    @Test
    @DisplayName("유효한 ID 목록으로 해시태그 리스트를 성공적으로 반환")
    void findHashtagsByIds_validIds_shouldReturnHashtags() {
        List<Long> ids = Arrays.asList(1L, 100L, 200L);
        List<Hashtag> hashtags = Hashtag.findHashtagsByIds(ids);

        assertEquals(3, hashtags.size());
        assertEquals(Hashtag.CLEAR, hashtags.get(0));
        assertEquals(Hashtag.EXCITED, hashtags.get(1));
        assertEquals(Hashtag.FRIEND, hashtags.get(2));
    }

    @Test
    @DisplayName("존재하지 않는 ID가 포함된 목록으로 HashtagNotFoundException 예외 발생")
    void findHashtagsByIds_invalidId_shouldThrowException() {
        List<Long> ids = Arrays.asList(1L, 2L, -1L);

        assertThatThrownBy(() -> Hashtag.findHashtagsByIds(ids))
                .isInstanceOf(HashtagNotFoundException.class)
                .hasMessage("Hashtag not found");
    }

    @Test
    @DisplayName("유효한 ID로 해시태그를 성공적으로 반환")
    void findHashtagById_validId_shouldReturnHashtag() {
        Hashtag hashtag = Hashtag.findHashtagById(1L);
        assertEquals(Hashtag.CLEAR, hashtag);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 HashtagNotFoundException 예외 발생")
    void findHashtagById_invalidId_shouldThrowException() {
        assertThatThrownBy(() -> Hashtag.findHashtagById(-1L))
                .isInstanceOf(HashtagNotFoundException.class)
                .hasMessage("Hashtag not found");
    }

    @Test
    @DisplayName("각 카테고리의 해시태그 ID가 올바른 범위에 있는지 테스트")
    void hashtagIds_shouldBeInCorrectRange() {
        for (Hashtag hashtag : Hashtag.values()) {
            long id = hashtag.getId();
            switch (hashtag.getHashtagType()) {
                case WEATHER:
                    assertTrue(id >= 1 && id <= 99, "Weather hashtags should have IDs between 1 and 99");
                    break;
                case EMOTION:
                    assertTrue(id >= 100 && id <= 199, "Emotion hashtags should have IDs between 100 and 199");
                    break;
                case MET_PERSON:
                    assertTrue(id >= 200 && id <= 299, "Met person hashtags should have IDs between 200 and 299");
                    break;
                default:
                    fail("Unknown hashtag type");
            }
        }
    }
}
