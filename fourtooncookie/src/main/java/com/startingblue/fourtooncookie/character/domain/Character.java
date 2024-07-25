package com.startingblue.fourtooncookie.character.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

@Entity
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Character {

    @Id @GeneratedValue
    @Column(name = "character_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private CharacterVisionType characterVisionType;

    @NotNull
    private String name;

    @NotNull
    private URL selectionThumbnailUrl;

    public Character(final CharacterVisionType characterVisionType, final String name, final URL selectionThumbnailUrl) {
        this.characterVisionType = characterVisionType;
        this.name = name;
        this.selectionThumbnailUrl = selectionThumbnailUrl;
    }

    public void update(final CharacterVisionType characterVisionType, final String name, final URL selectionThumbnailUrl) {
        this.characterVisionType = characterVisionType;
        this.name = name;
        this.selectionThumbnailUrl = selectionThumbnailUrl;
    }
}
