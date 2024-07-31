package com.startingblue.fourtooncookie.character.domain;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CharacterTest {

    @Mock
    private Artwork artwork;

    private URL validUrl;
    private URL newUrl;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        validUrl = new URL("http://example.com/thumbnail.png");
        newUrl = new URL("http://example.com/new-thumbnail.png");
    }

    @Test
    @DisplayName("유효한 Character 객체 생성")
    public void validCharacterCreation() {
        CharacterVisionType visionType = CharacterVisionType.DALL_E_3;
        PaymentType paymentType = PaymentType.FREE;
        String name = "ValidName";
        String basePrompt = "Base Prompt";

        Character character = Character.builder()
                .characterVisionType(visionType)
                .paymentType(paymentType)
                .name(name)
                .artwork(artwork)
                .selectionThumbnailUrl(validUrl)
                .basePrompt(basePrompt)
                .build();

        assertThat(character).isNotNull();
        assertThat(character.getCharacterVisionType()).isEqualTo(visionType);
        assertThat(character.getPaymentType()).isEqualTo(paymentType);
        assertThat(character.getName()).isEqualTo(name);
        assertThat(character.getArtwork()).isEqualTo(artwork);
        assertThat(character.getSelectionThumbnailUrl()).isEqualTo(validUrl);
        assertThat(character.getBasePrompt()).isEqualTo(basePrompt);
    }

    @Test
    @DisplayName("이름이 빈 문자열인 Character 객체 생성시 ConstraintViolationException")
    public void testInvalidCharacterCreation_NameBlank() {
        CharacterVisionType visionType = CharacterVisionType.DALL_E_3;
        PaymentType paymentType = PaymentType.FREE;
        String name = "";
        String basePrompt = "Base Prompt";

        assertThrows(ConstraintViolationException.class, () -> {
            Character.builder()
                    .characterVisionType(visionType)
                    .paymentType(paymentType)
                    .name(name)
                    .artwork(artwork)
                    .selectionThumbnailUrl(validUrl)
                    .basePrompt(basePrompt)
                    .build();
        });
    }

    @Test
    @DisplayName("Artwork가 null인 Character 객체 생성 시 ConstraintViolationException")
    public void testInvalidCharacterCreation_ArtworkNull() {
        CharacterVisionType visionType = CharacterVisionType.DALL_E_3;
        PaymentType paymentType = PaymentType.FREE;
        String name = "ValidName";
        String basePrompt = "Base Prompt";

        assertThrows(ConstraintViolationException.class, () -> {
            Character.builder()
                    .characterVisionType(visionType)
                    .paymentType(paymentType)
                    .name(name)
                    .artwork(null)
                    .selectionThumbnailUrl(validUrl)
                    .basePrompt(basePrompt)
                    .build();
        });
    }

    @Test
    @DisplayName("Artwork 제목이 빈 값 또는 1글자 이하일 때 Character 객체 생성 시 ConstraintViolationException")
    public void testInvalidCharacterCreation_ArtworkTitleTooShort() {
        CharacterVisionType visionType = CharacterVisionType.DALL_E_3;
        PaymentType paymentType = PaymentType.FREE;
        String name = "ValidName";
        String basePrompt = "Base Prompt";

        assertThrows(ConstraintViolationException.class, () -> {
            Character.builder()
                    .characterVisionType(visionType)
                    .paymentType(paymentType)
                    .name(name)
                    .artwork(new Artwork("", new URL("http://example.com/new-thumbnail.png")))
                    .selectionThumbnailUrl(validUrl)
                    .basePrompt(basePrompt)
                    .build();
        });
    }

    @Test
    @DisplayName("Artwork 제목이 255자 이상일 때 Character 객체 생성 시 ConstraintViolationException")
    public void testInvalidCharacterCreation_ArtworkTitleTooLong() {
        CharacterVisionType visionType = CharacterVisionType.DALL_E_3;
        PaymentType paymentType = PaymentType.FREE;
        String name = "ValidName";
        String basePrompt = "Base Prompt";
        String longTitle = "a".repeat(256);

        assertThrows(ConstraintViolationException.class, () -> {
            Character.builder()
                    .characterVisionType(visionType)
                    .paymentType(paymentType)
                    .name(name)
                    .artwork(new Artwork(longTitle, new URL("http://example.com/new-thumbnail.png")))
                    .selectionThumbnailUrl(validUrl)
                    .basePrompt(basePrompt)
                    .build();
        });
    }

    @Test
    @DisplayName("Character 객체 업데이트 테스트")
    public void testUpdateCharacter() {
        CharacterVisionType initialVisionType = CharacterVisionType.DALL_E_3;
        PaymentType initialPaymentType = PaymentType.FREE;
        String initialName = "OldName";
        String initialBasePrompt = "Old Prompt";

        Character character = Character.builder()
                .characterVisionType(initialVisionType)
                .paymentType(initialPaymentType)
                .name(initialName)
                .artwork(artwork)
                .selectionThumbnailUrl(validUrl)
                .basePrompt(initialBasePrompt)
                .build();

        CharacterVisionType newVisionType = CharacterVisionType.DALL_E_3;
        PaymentType newPaymentType = PaymentType.PAID;
        String newName = "NewName";
        String newBasePrompt = "New Prompt";

        character.update(newVisionType, newPaymentType, artwork, newName, newUrl, newBasePrompt);

        assertThat(character.getCharacterVisionType()).isEqualTo(newVisionType);
        assertThat(character.getPaymentType()).isEqualTo(newPaymentType);
        assertThat(character.getName()).isEqualTo(newName);
        assertThat(character.getArtwork()).isEqualTo(artwork);
        assertThat(character.getSelectionThumbnailUrl()).isEqualTo(newUrl);
        assertThat(character.getBasePrompt()).isEqualTo(newBasePrompt);
    }

    @Test
    @DisplayName("업데이트 시 이름이 빈 문자열일 때 Character 객체 업데이트 테스트")
    public void testUpdateCharacter_InvalidName() {
        CharacterVisionType initialVisionType = CharacterVisionType.DALL_E_3;
        PaymentType initialPaymentType = PaymentType.FREE;
        String initialName = "ValidName";
        String initialBasePrompt = "Base Prompt";

        Character character = Character.builder()
                .characterVisionType(initialVisionType)
                .paymentType(initialPaymentType)
                .name(initialName)
                .artwork(artwork)
                .selectionThumbnailUrl(validUrl)
                .basePrompt(initialBasePrompt)
                .build();

        CharacterVisionType newVisionType = CharacterVisionType.DALL_E_3;
        PaymentType newPaymentType = PaymentType.PAID;
        String newName = "";
        String newBasePrompt = "New Prompt";

        assertThrows(ConstraintViolationException.class, () -> {
            character.update(newVisionType, newPaymentType, artwork, newName, validUrl, newBasePrompt);
        });
    }
}
