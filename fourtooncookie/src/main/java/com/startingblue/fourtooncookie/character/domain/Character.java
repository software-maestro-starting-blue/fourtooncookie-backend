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
    private ModelType modelType;

    private String name;

    private URL selectionThumbnailUrl;

    public Character(final ModelType modelType, final String name, final URL selectionThumbnailUrl) {
        this.modelType = modelType;
        this.name = name;
        this.selectionThumbnailUrl = selectionThumbnailUrl;
    }

    public void changeModelType(final ModelType modelType) {
        this.modelType = modelType;
    }

    public void changeName(final String name) {
        this.name = name;
    }

    public void changeSelectionThumbnailUrl(final URL selectionThumbnailUrl) {
        this.selectionThumbnailUrl = selectionThumbnailUrl;
    }
}
