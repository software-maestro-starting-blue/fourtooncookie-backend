package com.startingblue.fourtooncookie.character.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    private String selectionThumbnailUrl;

    private String calendarThumbnailUrl;

    public Character(final ModelType modelType, final String name, final String selectionThumbnailUrl, final String calendarThumbnailUrl) {
        this(null, modelType, name, selectionThumbnailUrl, calendarThumbnailUrl);
    }

    public Character(final Long id, final ModelType modelType, final String name, final String selectionThumbnailUrl, final String calendarThumbnailUrl) {
        this.id = id;
        this.modelType = modelType;
        this.name = name;
        this.selectionThumbnailUrl = selectionThumbnailUrl;
        this.calendarThumbnailUrl = calendarThumbnailUrl;
    }

    public void changeModelType(final ModelType modelType) {
        this.modelType = modelType;
    }

    public void changeName(final String name) {
        this.name = name;
    }

    public void changeSelectionThumbnailUrl(final String selectionThumbnailUrl) {
        this.selectionThumbnailUrl = selectionThumbnailUrl;
    }

    public void changeCalendarThumbnailUrl(final String calendarThumbnailUrl) {
        this.calendarThumbnailUrl = calendarThumbnailUrl;
    }
}
