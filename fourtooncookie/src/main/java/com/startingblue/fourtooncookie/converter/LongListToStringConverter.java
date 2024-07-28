package com.startingblue.fourtooncookie.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
@Component
public class LongListToStringConverter implements AttributeConverter<List<Long>, String> {

    private static final String EMPTY_LIST = "";

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        if (isAttributeEmpty(attribute)) {
            return EMPTY_LIST;
        }

        return attribute.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (isDBColumnEmpty(dbData)) {
            return List.of();
        }
        return Arrays.stream(dbData.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    private static boolean isAttributeEmpty(List<Long> attribute) {
        return attribute == null || attribute.isEmpty();
    }

    private static boolean isDBColumnEmpty(String dbData) {
        return dbData == null || dbData.isEmpty();
    }
}
