package com.startingblue.fourtooncookie.artwork.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ArtworkTest {

    static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("유효한 작품 객체 생성 테스트")
    @Test
    void createValidArtwork() throws MalformedURLException {
        // Given
        String title = "Valid Title";
        URL thumbnailUrl = new URL("http://test.com/image.jpg");

        // When
        Artwork artwork = new Artwork(title, thumbnailUrl);

        // Then
        assertThat(artwork.getTitle()).isEqualTo(title);
        assertThat(artwork.getThumbnailUrl()).isEqualTo(thumbnailUrl);
    }

    @DisplayName("유효하지 않은 작품명으로 작품 객체 생성 시도시 예외 발생")
    @Test
    void createArtworkWithInvalidTitle() throws MalformedURLException {
        // Given
        String invalidTitle = "";
        URL thumbnailUrl = new URL("http://test.com/image.jpg");

        // When
        Artwork artwork = new Artwork(invalidTitle, thumbnailUrl);
        Set<ConstraintViolation<Artwork>> violations = validator.validate(artwork);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Artwork> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("작품명의 글자수는 1에서 255자 이내여야 합니다.");
    }

    @DisplayName("유효하지 않은 URL로 작품 객체 생성 시도시 예외 발생")
    @Test
    void createArtworkWithInvalidUrl() {
        // Given
        String title = "Valid Title";
        URL invalidUrl = null;

        // When
        Artwork artwork = new Artwork(title, invalidUrl);
        Set<ConstraintViolation<Artwork>> violations = validator.validate(artwork);

        // Then
        assertThat(violations).hasSize(1);
        ConstraintViolation<Artwork> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("작품 썸네일 URL이 존재해야 합니다.");
    }

    @DisplayName("작품 객체 업데이트 테스트")
    @Test
    void updateArtwork() throws MalformedURLException {
        // Given
        String oldTitle = "Old Title";
        URL oldUrl = new URL("http://test.com/oldimage.jpg");
        Artwork artwork = new Artwork(oldTitle, oldUrl);

        String newTitle = "New Title";
        URL newUrl = new URL("http://test.com/newimage.jpg");

        // When
        artwork.update(newTitle, newUrl);

        // Then
        assertThat(artwork.getTitle()).isEqualTo(newTitle);
        assertThat(artwork.getThumbnailUrl()).isEqualTo(newUrl);
    }
}
