package com.startingblue.fourtooncookie.converter;

import com.startingblue.fourtooncookie.diary.exception.DiaryConversionException;
import com.startingblue.fourtooncookie.diary.domain.converter.DiaryUrlListToStringConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DiaryUrlListToStringConverterTest {

    private final DiaryUrlListToStringConverter converter = new DiaryUrlListToStringConverter();

    @Test
    @DisplayName("List<URL>을 String으로 변환 (값 포함)")
    void testConvertToDatabaseColumn() throws MalformedURLException {
        List<URL> input = List.of(new URL("http://example.com"), new URL("http://example.org"));
        String expected = "http://example.com,http://example.org";
        String actual = converter.convertToDatabaseColumn(input);
        System.out.println("Test ConvertToDatabaseColumn: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("빈 List<URL>을 빈 String으로 변환")
    void testConvertToDatabaseColumnEmpty() {
        List<URL> input = List.of();
        String expected = "";
        String actual = converter.convertToDatabaseColumn(input);
        System.out.println("Test ConvertToDatabaseColumnEmpty: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("null List<URL>을 빈 String으로 변환")
    void testConvertToDatabaseColumnNull() {
        List<URL> input = null;
        String expected = "";
        String actual = converter.convertToDatabaseColumn(input);
        System.out.println("Test ConvertToDatabaseColumnNull: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("String을 List<URL>으로 변환 (값 포함)")
    void testConvertToEntityAttribute() throws MalformedURLException {
        String input = "http://example.com,http://example.org";
        List<URL> expected = List.of(new URL("http://example.com"), new URL("http://example.org"));
        List<URL> actual = converter.convertToEntityAttribute(input);
        System.out.println("Test ConvertToEntityAttribute: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("String을 List<URL>으로 변환 (공백 포함)")
    void testConvertToEntityAttributeTrim() throws MalformedURLException {
        String input = "     http://example.com, http://example.org   , http://example.org";
        List<URL> expected = List.of(new URL("http://example.com"), new URL("http://example.org"),
                new URL("http://example.com"));
        List<URL> actual = converter.convertToEntityAttribute(input);
        System.out.println("Test ConvertToEntityAttribute: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("빈 String을 빈 List<URL>으로 변환")
    void testConvertToEntityAttributeEmpty() {
        String input = "";
        List<URL> expected = List.of();
        List<URL> actual = converter.convertToEntityAttribute(input);
        System.out.println("Test ConvertToEntityAttributeEmpty: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("null String을 빈 List<URL>으로 변환")
    void testConvertToEntityAttributeNull() {
        String input = null;
        List<URL> expected = List.of();
        List<URL> actual = converter.convertToEntityAttribute(input);
        System.out.println("Test ConvertToEntityAttributeNull: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("잘못된 형식의 String을 List<URL>으로 변환 시 예외 발생")
    void testConvertToEntityAttributeMalformed() {
        String input = "http://example.com,http://example.org,malformed_url";
        assertThatThrownBy(() -> {
            converter.convertToEntityAttribute(input);
        }).isInstanceOf(DiaryConversionException.class)
                .hasMessageContaining("Error converting String to List<URL>");
    }
}
