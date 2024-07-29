package com.startingblue.fourtooncookie.character.domain;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

@Entity
@Slf4j
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Character {

    @Id @GeneratedValue
    @Column(name = "character_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private CharacterVisionType characterVisionType;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    @NotBlank
    private String name;

    @NotNull
    private URL selectionThumbnailUrl;

    @NotBlank
    private String basePrompt;

    public Character update(final CharacterVisionType characterVisionType, final PaymentType paymentType, final Artwork artwork, final String name, final URL selectionThumbnailUrl, final String basePrompt) {
        this.characterVisionType = characterVisionType;
        this.paymentType = paymentType;
        this.artwork = artwork;
        this.name = name;
        this.selectionThumbnailUrl = selectionThumbnailUrl;
        this.basePrompt = basePrompt;
        return this;
    }
}
