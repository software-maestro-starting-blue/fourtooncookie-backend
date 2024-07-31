package com.startingblue.fourtooncookie.artwork.domain;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
public class ArtworkTest {

    private URL validUrl;
    private URL newUrl;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        validUrl = new URL("http://example.com/thumbnail.png");
        newUrl = new URL("http://example.com/new-thumbnail.png");
    }

    @Test
    @DisplayName("유효한 Artwork 객체 생성")
    public void validArtworkCreation() {
        String title = "ValidTitle";

        Artwork artwork = new Artwork(title, validUrl);

        assertThat(artwork).isNotNull();
        assertThat(artwork.getTitle()).isEqualTo(title);
        assertThat(artwork.getThumbnailUrl()).isEqualTo(validUrl);
    }

    @Test
    @DisplayName("제목이 빈 문자열인 Artwork 객체 생성시 ConstraintViolationException")
    public void testInvalidArtworkCreation_TitleBlank() {
        String title = "";

        assertThatThrownBy(() -> new Artwork(title, validUrl))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("작품명은 필수 입니다.");
    }

    @Test
    @DisplayName("제목이 null인 Artwork 객체 생성시 ConstraintViolationException")
    public void testInvalidArtworkCreation_TitleNull() {
        String title = null;

        assertThatThrownBy(() -> new Artwork(title, validUrl))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("작품명은 필수 입니다.");
    }

    @Test
    @DisplayName("썸네일 URL이 null인 Artwork 객체 생성 시 ConstraintViolationException")
    public void testInvalidArtworkCreation_ThumbnailUrlNull() {
        String title = "ValidTitle";

        assertThatThrownBy(() -> new Artwork(title, null))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("작품 썸네일 URL이 존재해야 합니다.");
    }

    @Test
    @DisplayName("제목이 256자 이상인 Artwork 객체 생성 시 ConstraintViolationException")
    public void testInvalidArtworkCreation_TitleTooLong() {
        String longTitle = "a".repeat(256);

        assertThatThrownBy(() -> new Artwork(longTitle, validUrl))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("작품명의 글자수는 1에서 255자 이내여야 합니다.");
    }

    @Test
    @DisplayName("Artwork 객체 업데이트 테스트")
    public void testUpdateArtwork() {
        String initialTitle = "OldTitle";
        Artwork artwork = new Artwork(initialTitle, validUrl);

        String newTitle = "NewTitle";

        artwork.update(newTitle, newUrl);

        assertThat(artwork.getTitle()).isEqualTo(newTitle);
        assertThat(artwork.getThumbnailUrl()).isEqualTo(newUrl);
    }

    @Test
    @DisplayName("업데이트 시 제목이 빈 문자열일 때 Artwork 객체 업데이트 테스트")
    public void testUpdateArtwork_InvalidTitle() {
        String initialTitle = "OldTitle";
        Artwork artwork = new Artwork(initialTitle, validUrl);

        String newTitle = "";

        assertThatThrownBy(() -> {
            artwork.update(newTitle, newUrl);
        }).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("작품명은 필수 입니다.");
    }

    @Test
    @DisplayName("업데이트 시 썸네일 URL이 null일 때 Artwork 객체 업데이트 테스트")
    public void testUpdateArtwork_InvalidThumbnailUrl() {
        String initialTitle = "OldTitle";
        Artwork artwork = new Artwork(initialTitle, validUrl);

        URL newThumbnailUrl = null;

        assertThatThrownBy(() -> {
            artwork.update(initialTitle, newThumbnailUrl);
        }).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("작품 썸네일 URL이 존재해야 합니다.");
    }

    @Test
    @DisplayName("업데이트 시 제목이 256자 이상일 때 Artwork 객체 업데이트 테스트")
    public void testUpdateArtwork_TitleTooLong() {
        String initialTitle = "OldTitle";
        Artwork artwork = new Artwork(initialTitle, validUrl);

        String longTitle = "a".repeat(256);

        assertThatThrownBy(() -> {
            artwork.update(longTitle, newUrl);
        }).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("작품명의 글자수는 1에서 255자 이내여야 합니다.");
    }
}
