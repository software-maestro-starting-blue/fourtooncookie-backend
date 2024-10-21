package com.startingblue.fourtooncookie.translation;

import com.startingblue.fourtooncookie.translation.annotation.TranslatableField;
import com.startingblue.fourtooncookie.translation.domain.Translation;
import com.startingblue.fourtooncookie.translation.domain.TranslationId;
import com.startingblue.fourtooncookie.translation.exception.TranslationDuplicateException;
import com.startingblue.fourtooncookie.translation.exception.TranslationNotFoundException;
import com.startingblue.fourtooncookie.translation.exception.TranslationObjectClassIdNotFoundException;
import io.sentry.Sentry;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class TranslationService {

    private final TranslationRepository translationRepository;

    private static final Map<String, List<Field>> cachedTranslatableField = new ConcurrentHashMap<>();

    public <T> T getTranslatedObject(T object, Locale locale) {
        getTranslatableFields(object).forEach(field -> {
            try {
                String fieldName = field.getName();
                Object fieldValue = field.get(object);

                if (!(fieldValue instanceof String))
                    return;

                String translatedValue = getTranslationContent(object, fieldName, locale);

                field.set(object, translatedValue);
            } catch (Exception ignored) {
                Sentry.captureException(ignored);
            }
        });

        return object;
    }

    public String getTranslationContent(TranslationId translationId) {
        Translation translation = translationRepository.findById(translationId)
                .orElseThrow(TranslationNotFoundException::new);

        return translation.getContent();
    }

    public String getTranslationContent(String className, String fieldName, Long classId, Locale locale) {
        return getTranslationContent(new TranslationId(className, fieldName, classId, locale));
    }

    public String getTranslationContent(Object object, String fieldName, Locale locale) {
        String className = getClassName(object);
        Long classId = getClassId(object);
        return getTranslationContent(className, fieldName, classId, locale);
    }

    public void addTranslation(Object object, String fieldName, Locale locale, String content) {
        String className = getClassName(object);
        Long classId = getClassId(object);

        TranslationId translationId = new TranslationId(className, fieldName, classId, locale);

        if (!isTranslationExists(translationId)) {
            throw new TranslationDuplicateException();
        }

        Translation translation = Translation.builder()
                .translationId(translationId)
                .content(content)
                .build();

        translationRepository.save(translation);
    }

    private boolean isTranslationExists(TranslationId translationId) {
        return translationRepository.existsById(translationId);
    }

    private String getClassName(Object object) {
        return object.getClass().getSimpleName();
    }

    private Long getClassId(Object object) {
        try {
            return (Long) Arrays.stream(object.getClass().getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Id.class))
                    .findFirst().get().get(object);
        } catch (Exception e) {
            throw new TranslationObjectClassIdNotFoundException();
        }
    }

    private List<Field> getTranslatableFields(Object object) {
        String className = getClassName(object);

        if (cachedTranslatableField.containsKey(className)) {
            return cachedTranslatableField.get(className);
        }

        List<Field> translatableFields = new ArrayList<>();
        Arrays.stream(object.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(TranslatableField.class))
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        translatableFields.add(field);
                    } catch (Exception ignored) {
                        Sentry.captureException(ignored);
                    }
                });

        cachedTranslatableField.put(className, translatableFields);

        return translatableFields;
    }
}
