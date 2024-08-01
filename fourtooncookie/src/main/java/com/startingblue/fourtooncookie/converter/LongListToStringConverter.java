package com.startingblue.fourtooncookie.converter;

import com.startingblue.fourtooncookie.converter.exception.ConversionException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
@Component
public class LongListToStringConverter implements AttributeConverter<List<Long>, String> {

    private static final String EMPTY_LIST_STRING = "";

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        if (isAttributeEmpty(attribute)) {
            return EMPTY_LIST_STRING;
        }

        try {
            return attribute.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        } catch (Exception e) {
            throw new ConversionException("Error converting List<Long> to String", e);
        }
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (isDBColumnEmpty(dbData)) {
            return List.of();
        }

        try {
            return Arrays.stream(dbData.split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new ConversionException("Error converting String to List<Long>", e);
        }
    }

    private static boolean isAttributeEmpty(List<Long> attribute) {
        return attribute == null || attribute.isEmpty();
    }

    private static boolean isDBColumnEmpty(String dbData) {
        return dbData == null || dbData.isEmpty();
    }
}
