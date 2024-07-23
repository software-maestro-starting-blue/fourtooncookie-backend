package com.startingblue.fourtooncookie.character.domain;

import jakarta.persistence.*;
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
    private CharacterType characterType;

    private String name;

    private URL selectionThumbnailUrl;

    public Character(final CharacterType characterType, final String name, final URL selectionThumbnailUrl) {
        this.characterType = characterType;
        this.name = name;
        this.selectionThumbnailUrl = selectionThumbnailUrl;
    }

    public void update(final  CharacterType characterType, final String name, final URL selectionThumbnailUrl) {
        this.characterType = characterType;
        this.name = name;
        this.selectionThumbnailUrl = selectionThumbnailUrl;
    }
}
