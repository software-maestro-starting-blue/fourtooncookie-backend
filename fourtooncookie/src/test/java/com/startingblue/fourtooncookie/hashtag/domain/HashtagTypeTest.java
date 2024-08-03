package com.startingblue.fourtooncookie.hashtag.domain;

import com.startingblue.fourtooncookie.hashtag.exception.HashtagIdInvalidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HashtagTypeTest {

    @ParameterizedTest
    @ValueSource(longs = {1L, 50L, 99L})
    @DisplayName("유효한 날씨 해시태그 ID는 예외를 발생시키지 않아야 한다")
    void validateWeatherHashtagId_ShouldNotThrowException_WhenIdIsValid(Long hashtagId) {
        assertDoesNotThrow(() -> HashtagType.WEATHER.validateHashtagId(hashtagId));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 100L, 150L})
    @DisplayName("유효하지 않은 날씨 해시태그 ID는 예외를 발생시켜야 한다")
    void validateWeatherHashtagId_ShouldThrowException_WhenIdIsInvalid(Long hashtagId) {
        assertThrows(HashtagIdInvalidException.class, () -> HashtagType.WEATHER.validateHashtagId(hashtagId));
    }

    @ParameterizedTest
    @ValueSource(longs = {100L, 150L, 199L})
    @DisplayName("유효한 감정 해시태그 ID는 예외를 발생시키지 않아야 한다")
    void validateEmotionHashtagId_ShouldNotThrowException_WhenIdIsValid(Long hashtagId) {
        assertDoesNotThrow(() -> HashtagType.EMOTION.validateHashtagId(hashtagId));
    }

    @ParameterizedTest
    @ValueSource(longs = {99L, 200L, 250L})
    @DisplayName("유효하지 않은 감정 해시태그 ID는 예외를 발생시켜야 한다")
    void validateEmotionHashtagId_ShouldThrowException_WhenIdIsInvalid(Long hashtagId) {
        assertThrows(HashtagIdInvalidException.class, () -> HashtagType.EMOTION.validateHashtagId(hashtagId));
    }

    @ParameterizedTest
    @ValueSource(longs = {200L, 250L, 299L})
    @DisplayName("유효한 만난 사람 해시태그 ID는 예외를 발생시키지 않아야 한다")
    void validateMetPersonHashtagId_ShouldNotThrowException_WhenIdIsValid(Long hashtagId) {
        assertDoesNotThrow(() -> HashtagType.MET_PERSON.validateHashtagId(hashtagId));
    }

    @ParameterizedTest
    @ValueSource(longs = {199L, 300L, 350L})
    @DisplayName("유효하지 않은 만난 사람 해시태그 ID는 예외를 발생시켜야 한다")
    void validateMetPersonHashtagId_ShouldThrowException_WhenIdIsInvalid(Long hashtagId) {
        assertThrows(HashtagIdInvalidException.class, () -> HashtagType.MET_PERSON.validateHashtagId(hashtagId));
    }
}
