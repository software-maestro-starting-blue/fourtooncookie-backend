package com.startingblue.fourtooncookie.converter;

import com.startingblue.fourtooncookie.global.converter.exception.ConversionException;
import com.startingblue.fourtooncookie.diary.domain.converter.LongListToStringConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class LongListToStringConverterTest {

    private final LongListToStringConverter converter = new LongListToStringConverter();

    @Test
    @DisplayName("List<Long>을 String으로 변환 (값 포함)")
    void testConvertToDatabaseColumn() {
        List<Long> input = List.of(1L, 2L, 3L);
        String expected = "1,2,3";
        String actual = converter.convertToDatabaseColumn(input);
        System.out.println("Test ConvertToDatabaseColumn: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("빈 List<Long>을 String으로 변환")
    void testConvertToDatabaseColumnEmpty() {
        List<Long> input = List.of();
        String expected = "";
        String actual = converter.convertToDatabaseColumn(input);
        System.out.println("Test ConvertToDatabaseColumnEmpty: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("null List<Long>을 String으로 변환")
    void testConvertToDatabaseColumnNull() {
        List<Long> input = null;
        String expected = "";
        String actual = converter.convertToDatabaseColumn(input);
        System.out.println("Test ConvertToDatabaseColumnNull: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("String을 List<Long>으로 변환 (값 포함)")
    void testConvertToEntityAttribute() {
        String input = "1,2,3";
        List<Long> expected = List.of(1L, 2L, 3L);
        List<Long> actual = converter.convertToEntityAttribute(input);
        System.out.println("Test ConvertToEntityAttribute: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("빈 String을 List<Long>으로 변환")
    void testConvertToEntityAttributeEmpty() {
        String input = "";
        List<Long> expected = List.of();
        List<Long> actual = converter.convertToEntityAttribute(input);
        System.out.println("Test ConvertToEntityAttributeEmpty: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("null String을 List<Long>으로 변환")
    void testConvertToEntityAttributeNull() {
        String input = null;
        List<Long> expected = List.of();
        List<Long> actual = converter.convertToEntityAttribute(input);
        System.out.println("Test ConvertToEntityAttributeNull: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("잘못된 형식의 String을 List<Long>으로 변환 시 예외 발생")
    void testConvertToEntityAttributeMalformed() {
        String input = "1,2,abc";
        assertThatThrownBy(() -> {
            converter.convertToEntityAttribute(input);
        }).isInstanceOf(ConversionException.class)
                .hasMessageContaining("Error converting String to List<Long>");
    }
}
