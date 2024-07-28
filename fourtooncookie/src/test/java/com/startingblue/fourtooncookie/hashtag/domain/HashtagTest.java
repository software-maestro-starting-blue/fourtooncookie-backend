package com.startingblue.fourtooncookie.hashtag.domain;

import com.startingblue.fourtooncookie.hashtag.exception.HashtagNoSuchElementException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class HashtagTest {

    @Test
    @DisplayName("존재하는 해시태그를 찾는다.")
    public void testFindHashtagsByIds() {
        // Given
        List<Long> ids = Arrays.asList(1L, 2L);

        // When
        List<Hashtag> hashtags = Hashtag.findHashtagsByIds(ids);

        // Then
        assertEquals(2, hashtags.size());
        assertEquals(Hashtag.SAD, hashtags.get(0));
        assertEquals(Hashtag.DSAS, hashtags.get(1));
    }

    @Test
    @DisplayName("존재하지 않는 해시태그는 찾을 수 없다.")
    public void testFindHashtagById_NotFound() {
        // Given
        Long nonExistentId = -1L;

        // When & Then
        assertThrows(HashtagNoSuchElementException.class, () -> {
            Hashtag.findHashtagsByIds(List.of(nonExistentId));
        });
    }

}