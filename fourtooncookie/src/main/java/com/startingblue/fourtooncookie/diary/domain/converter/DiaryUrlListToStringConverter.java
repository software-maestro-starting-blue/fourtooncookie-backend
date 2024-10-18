package com.startingblue.fourtooncookie.diary.domain.converter;

import com.startingblue.fourtooncookie.diary.exception.DiaryConversionException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
@Component
public class DiaryUrlListToStringConverter implements AttributeConverter<List<URL>, String> {

    private static final String EMPTY_LIST_STRING = "";

    @Override
    public String convertToDatabaseColumn(List<URL> attribute) {
        if (isAttributeEmpty(attribute)) {
            return EMPTY_LIST_STRING;
        }

        try {
            return attribute.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        } catch (Exception e) {
            throw new DiaryConversionException("Error converting List<URL> to String", e);
        }
    }

    @Override
    public List<URL> convertToEntityAttribute(String dbData) {
        if (isDBColumnEmpty(dbData)) {
            return List.of();
        }

        try {
            return Arrays.stream(dbData.split(","))
                    .map(urlString -> {
                        try {
                            return new URL(urlString);
                        } catch (MalformedURLException e) {
                            throw new DiaryConversionException("Malformed URL: " + urlString, e);
                        }
                    })
                    .toList();
        } catch (Exception e) {
            throw new DiaryConversionException("Error converting String to List<URL>", e);
        }
    }

    private static boolean isAttributeEmpty(List<URL> attribute) {
        return attribute == null || attribute.isEmpty();
    }

    private static boolean isDBColumnEmpty(String dbData) {
        return dbData == null || dbData.isEmpty();
    }
}
