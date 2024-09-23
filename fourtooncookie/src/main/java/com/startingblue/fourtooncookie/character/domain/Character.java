package com.startingblue.fourtooncookie.character.domain;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.global.domain.PaymentType;
import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Objects;
import java.util.Set;

@Entity
@Slf4j
@Table(name = "`character`")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "캐릭터 비전 유형은 필수 입니다")
    private CharacterVisionType characterVisionType;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "캐릭터 결제 유형은 필수 입니다.")
    private PaymentType paymentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id")
    @NotNull(message = "캐릭터 작품은 필수 입니다.")
    private Artwork artwork;

    @NotBlank(message = "캐릭터 이름은 필수 입니다.")
    @Size(min = 1, max = 255, message = "캐릭터 이름은 1자 이상 255자 이내여야 합니다.")
    private String name;

    @NotNull(message = "캐릭터 선택 썸네일 URL은 필수 입니다.")
    private URL selectionThumbnailUrl;

    @NotBlank(message = "캐릭터 기본 프롬프트는 필수 입니다.")
    private String basePrompt;

    public static CharacterBuilder builder() {
        return new CustomCharacterBuilder();
    }

    public Character update(final CharacterVisionType characterVisionType, final PaymentType paymentType, final Artwork artwork, final String name, final URL selectionThumbnailUrl, final String basePrompt) {
        this.characterVisionType = characterVisionType;
        this.paymentType = paymentType;
        this.artwork = artwork;
        this.name = name;
        this.selectionThumbnailUrl = selectionThumbnailUrl;
        this.basePrompt = basePrompt;
        validate();
        return this;
    }

    private void validate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Character>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private static class CustomCharacterBuilder extends CharacterBuilder {
        @Override
        public Character build() {
            Character character = super.build();
            character.validate();
            return character;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Character character = (Character) o;
        return Objects.equals(id, character.id) && characterVisionType == character.characterVisionType && paymentType == character.paymentType && Objects.equals(artwork, character.artwork) && Objects.equals(name, character.name) && Objects.equals(selectionThumbnailUrl, character.selectionThumbnailUrl) && Objects.equals(basePrompt, character.basePrompt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, characterVisionType, paymentType, artwork, name, selectionThumbnailUrl, basePrompt);
    }
}
