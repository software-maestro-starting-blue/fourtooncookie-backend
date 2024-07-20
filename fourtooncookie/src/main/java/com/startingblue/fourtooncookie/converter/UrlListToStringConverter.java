package com.startingblue.fourtooncookie.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
@Service
public class UrlListToStringConverter implements AttributeConverter<List<URL>, String> {

    @Override
    public String convertToDatabaseColumn(List<URL> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return attribute.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<URL> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(dbData.split(","))
                .map(urlString -> {
                    try {
                        return new URL(urlString);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException("Malformed URL: " + urlString, e);
                    }
                })
                .toList();
    }
}
