package com.startingblue.fourtooncookie.character.domain;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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

        assertThatThrownBy(() -> {
            Character.builder()
                    .characterVisionType(visionType)
                    .paymentType(paymentType)
                    .name(name)
                    .artwork(artwork)
                    .selectionThumbnailUrl(validUrl)
                    .basePrompt(basePrompt)
                    .build();
        }).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("캐릭터 이름은 필수 입니다.");
    }

    @Test
    @DisplayName("Artwork가 null인 Character 객체 생성 시 ConstraintViolationException")
    public void testInvalidCharacterCreation_ArtworkNull() {
        CharacterVisionType visionType = CharacterVisionType.DALL_E_3;
        PaymentType paymentType = PaymentType.FREE;
        String name = "ValidName";
        String basePrompt = "Base Prompt";

        assertThatThrownBy(() -> {
            Character.builder()
                    .characterVisionType(visionType)
                    .paymentType(paymentType)
                    .name(name)
                    .artwork(null)
                    .selectionThumbnailUrl(validUrl)
                    .basePrompt(basePrompt)
                    .build();
        }).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("캐릭터 작품은 필수 입니다.");
    }

    @Test
    @DisplayName("썸네일 URL이 null인 Character 객체 생성 시 ConstraintViolationException")
    public void testInvalidCharacterCreation_ThumbnailUrlNull() {
        CharacterVisionType visionType = CharacterVisionType.DALL_E_3;
        PaymentType paymentType = PaymentType.FREE;
        String name = "ValidName";
        String basePrompt = "Base Prompt";

        assertThatThrownBy(() -> {
            Character.builder()
                    .characterVisionType(visionType)
                    .paymentType(paymentType)
                    .name(name)
                    .artwork(artwork)
                    .selectionThumbnailUrl(null)
                    .basePrompt(basePrompt)
                    .build();
        }).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("캐릭터 선택 썸네일 URL은 필수 입니다.");
    }

    @Test
    @DisplayName("기본 프롬프트가 빈 문자열인 Character 객체 생성 시 ConstraintViolationException")
    public void testInvalidCharacterCreation_BasePromptBlank() {
        CharacterVisionType visionType = CharacterVisionType.DALL_E_3;
        PaymentType paymentType = PaymentType.FREE;
        String name = "ValidName";
        String basePrompt = "";

        assertThatThrownBy(() -> {
            Character.builder()
                    .characterVisionType(visionType)
                    .paymentType(paymentType)
                    .name(name)
                    .artwork(artwork)
                    .selectionThumbnailUrl(validUrl)
                    .basePrompt(basePrompt)
                    .build();
        }).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("캐릭터 기본 프롬프트는 필수 입니다.");
    }

    @Test
    @DisplayName("캐릭터 결제 유형이 null인 Character 객체 생성 시 ConstraintViolationException")
    public void testInvalidCharacterCreation_PaymentTypeNull() {
        CharacterVisionType visionType = CharacterVisionType.DALL_E_3;
        String name = "ValidName";
        String basePrompt = "Base Prompt";

        assertThatThrownBy(() -> {
            Character.builder()
                    .characterVisionType(visionType)
                    .paymentType(null)
                    .name(name)
                    .artwork(artwork)
                    .selectionThumbnailUrl(validUrl)
                    .basePrompt(basePrompt)
                    .build();
        }).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("캐릭터 결제 유형은 필수 입니다.");
    }

    @Test
    @DisplayName("캐릭터 비전 유형이 null인 Character 객체 생성 시 ConstraintViolationException")
    public void testInvalidCharacterCreation_CharacterVisionTypeNull() {
        PaymentType paymentType = PaymentType.FREE;
        String name = "ValidName";
        String basePrompt = "Base Prompt";

        assertThatThrownBy(() -> {
            Character.builder()
                    .characterVisionType(null)
                    .paymentType(paymentType)
                    .name(name)
                    .artwork(artwork)
                    .selectionThumbnailUrl(validUrl)
                    .basePrompt(basePrompt)
                    .build();
        }).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("캐릭터 비전 유형은 필수 입니다");
    }

    @Test
    @DisplayName("Character 객체 업데이트 성공")
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
    @DisplayName("Character 업데이트 시 이름이 빈 문자열일 때 ConstraintViolationException")
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

        assertThatThrownBy(() -> {
            character.update(newVisionType, newPaymentType, artwork, newName, validUrl, newBasePrompt);
        }).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("캐릭터 이름은 필수 입니다.");
    }
}
