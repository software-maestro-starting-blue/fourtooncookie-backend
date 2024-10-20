package com.startingblue.fourtooncookie.translation.domain;

import jakarta.persistence.*;
import jakarta.validation.*;
import lombok.*;
import software.amazon.awssdk.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Translation {

    @EmbeddedId
    private TranslationId translationId;

    @NotNull
    private String content;

    public void update(String content) {
        this.content = content;

        validate();
    }

    public static TranslationBuilder builder() {
        return new CustomTranslationBuilder();
    }

    private static class CustomTranslationBuilder extends TranslationBuilder {
        @Override
        public Translation build() {
            Translation translation = super.build();
            translation.validate();
            return translation;
        }
    }

    private void validate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Translation>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Translation that = (Translation) o;
        return translationId.equals(that.translationId)
                && content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(translationId, content);
    }
}
