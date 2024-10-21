package com.startingblue.fourtooncookie.translation.domain;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;
import java.util.Objects;

@Getter
@Embeddable
@AllArgsConstructor
public class TranslationId {

    private String className;

    private String fieldName;

    private Long classId;

    private Locale locale;

    public TranslationId() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationId that = (TranslationId) o;
        return (Objects.equals(className, that.className)
                && Objects.equals(fieldName, that.fieldName)
                && Objects.equals(classId, that.classId)
                && Objects.equals(locale, that.locale));
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, fieldName, classId, locale);
    }
}
