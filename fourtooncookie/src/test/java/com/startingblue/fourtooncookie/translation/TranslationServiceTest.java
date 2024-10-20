package com.startingblue.fourtooncookie.translation;

import com.startingblue.fourtooncookie.translation.domain.Translation;
import com.startingblue.fourtooncookie.translation.domain.TranslationId;
import com.startingblue.fourtooncookie.translation.exception.TranslationNotFoundException;
import com.startingblue.fourtooncookie.translation.sample.SampleEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
class TranslationServiceTest {

    @Autowired
    private TranslationService translationService;

    @Autowired
    private TranslationRepository translationRepository;

    @BeforeEach
    void setUp() {
        translationRepository.deleteAllInBatch();
    }

    @DisplayName("언어 지원이 존재하는 경우에 번역이 성공하는가를 테스트")
    @Test
    void getTranslatedObject() {
        // given
        Long id = 1L;
        String defaultField = "기본값";
        String translatableFieldInEnglish = "english";
        String translatableField2InEnglish = "english2";
        String translatableFieldInFrench = "french";
        String translatableField2InFrench = "french";
        SampleEntity sampleEntity = new SampleEntity(id, defaultField, defaultField, defaultField, defaultField);

        // when
        String className = sampleEntity.getClass().getSimpleName();

        translationRepository.save(Translation.builder()
                .translationId(new TranslationId(className,  "translatableField", id, Locale.ENGLISH))
                .content(translatableFieldInEnglish)
                .build()
        );

        translationRepository.save(Translation.builder()
                .translationId(new TranslationId(className,  "translatableField", id, Locale.FRENCH))
                .content(translatableFieldInFrench)
                .build()
        );

        translationRepository.save(Translation.builder()
                .translationId(new TranslationId(className,  "translatableField2", id, Locale.ENGLISH))
                .content(translatableField2InEnglish)
                .build()
        );

        translationRepository.save(Translation.builder()
                .translationId(new TranslationId(className,  "translatableField2", id, Locale.FRENCH))
                .content(translatableField2InFrench)
                .build()
        );

        SampleEntity sampleEntityInEnglish = translationService.getTranslatedObject(sampleEntity, Locale.ENGLISH);

        SampleEntity sampleEntityInFrench = translationService.getTranslatedObject(sampleEntity, Locale.FRENCH);

        // then 1
        // Translatable Field가 변경되었는가
        assertThat(sampleEntityInEnglish.translatableField).isEqualTo(translatableFieldInEnglish);
        assertThat(sampleEntityInEnglish.translatableField2).isEqualTo(translatableField2InEnglish);
        // un Translatable Field가 변경되지 않았는가
        assertThat(sampleEntityInEnglish.unTranslatableField).isEqualTo(defaultField);
        assertThat(sampleEntityInEnglish.unTranslatableField2).isEqualTo(defaultField);

        // then 2
        // Translatable Field가 변경되었는가
        assertThat(sampleEntityInFrench.translatableField).isEqualTo(translatableFieldInFrench);
        assertThat(sampleEntityInFrench.translatableField2).isEqualTo(translatableField2InFrench);
        // un Translatable Field가 변경되지 않았는가
        assertThat(sampleEntityInFrench.unTranslatableField).isEqualTo(defaultField);
        assertThat(sampleEntityInFrench.unTranslatableField2).isEqualTo(defaultField);
    }

    @DisplayName("언어 지원이 존재하지 않는 경우에 번역이 안 되고 그대로 나오는가를 테스트")
    @Test
    void getTranslatedObjectDefault() {
        // given
        Long id = 1L;
        String defaultField = "기본값";
        String translatableFieldInEnglish = "english";
        String translatableField2InEnglish = "english2";
        SampleEntity sampleEntity = new SampleEntity(id, defaultField, defaultField, defaultField, defaultField);

        // when
        String className = sampleEntity.getClass().getSimpleName();

        translationRepository.save(Translation.builder()
                .translationId(new TranslationId(className,  "translatableField", id, Locale.ENGLISH))
                .content(translatableFieldInEnglish)
                .build()
        );

        translationRepository.save(Translation.builder()
                .translationId(new TranslationId(className,  "translatableField2", id, Locale.ENGLISH))
                .content(translatableField2InEnglish)
                .build()
        );

        SampleEntity sampleEntityInFrench = translationService.getTranslatedObject(sampleEntity, Locale.FRENCH);

        //then
        // Translatable Field가 변경되었는가 (변경되지 않았어야 함)
        assertThat(sampleEntityInFrench.translatableField).isEqualTo(defaultField);
        assertThat(sampleEntityInFrench.translatableField2).isEqualTo(defaultField);
        // un Translatable Field가 변경되지 않았는가
        assertThat(sampleEntityInFrench.unTranslatableField).isEqualTo(defaultField);
        assertThat(sampleEntityInFrench.unTranslatableField2).isEqualTo(defaultField);
    }

    @DisplayName("지원하는 TranslationId에 대하여 getTranslationContent가 작동하는 가 테스트")
    @Test
    void getTranslationContent() {
        // given
        Long id = 1L;
        String defaultField = "기본값";
        String translatableFieldName = "translatableField";
        String translatableFieldInEnglish = "english";
        SampleEntity sampleEntity = new SampleEntity(id, defaultField, defaultField, defaultField, defaultField);
        String className = sampleEntity.getClass().getSimpleName();

        TranslationId translationId = new TranslationId(className,  translatableFieldName, id, Locale.ENGLISH);

        translationRepository.save(Translation.builder()
                .translationId(translationId)
                .content(translatableFieldInEnglish)
                .build()
        );

        // when
        String content = translationService.getTranslationContent(translationId);
        String content2 = translationService.getTranslationContent(className, translatableFieldName, id, Locale.ENGLISH);
        String content3 = translationService.getTranslationContent(sampleEntity, translatableFieldName, Locale.ENGLISH);

        // then
        assertThat(content).isEqualTo(translatableFieldInEnglish);
        assertThat(content2).isEqualTo(translatableFieldInEnglish);
        assertThat(content3).isEqualTo(translatableFieldInEnglish);
    }

    @DisplayName("지원하지 않는 TranslationId에 대하여 getTranslationContent가 에러를 던지는 가에 대한 실패 테스트")
    @Test
    void getTranslationContentFailed() {
        // given
        Long id = 1L;
        Long wrongId = 2L;
        String defaultField = "기본값";

        SampleEntity sampleEntity = new SampleEntity(id, defaultField, defaultField, defaultField, defaultField);

        String className = sampleEntity.getClass().getSimpleName();
        String wrongClassName = "wrongClass";
        String translatableFieldName = "translatableField";
        String wrongTranslatableFieldName = "wrongField";
        String translatableFieldInEnglish = "english";

        TranslationId translationId = new TranslationId(className, translatableFieldName, id, Locale.ENGLISH);

        translationRepository.save(Translation.builder()
                .translationId(translationId)
                .content(translatableFieldInEnglish)
                .build()
        );

        // 가능한 모든 failed translation id를 저장하는 로직
        List<TranslationId> failedTranslationIds = new ArrayList<>();
        Stream.of(className, wrongClassName).forEach(currentClassName ->
            Stream.of(translatableFieldName, wrongTranslatableFieldName).forEach(currentFieldName ->
                Stream.of(id, wrongId).forEach(currentId ->
                    Stream.of(Locale.ENGLISH, Locale.KOREAN).forEach(currentLocale -> {
                        TranslationId currentTranslationId = new TranslationId(className, currentFieldName, id, currentLocale);
                        if (! currentTranslationId.equals(translationId)) {
                            failedTranslationIds.add(currentTranslationId);
                        }
                    })
                )
            )
        );

        // when & then
        failedTranslationIds.forEach(failedTranslationId ->
            assertThatThrownBy(() -> translationService.getTranslationContent(failedTranslationId))
                    .isInstanceOf(TranslationNotFoundException.class)
        );
    }

    @Test
    void addTranslation() {
        // TODO
    }
}