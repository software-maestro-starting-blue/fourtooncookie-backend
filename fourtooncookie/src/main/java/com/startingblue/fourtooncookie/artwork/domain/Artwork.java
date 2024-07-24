package com.startingblue.fourtooncookie.artwork.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

@Entity
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Artwork {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "art_work_id")
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotNull
    private URL thumbnailUrl;

    public Artwork(final String title, final URL thumbnailUrl) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void update(final String title, final URL thumbnailUrl) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
    }
}
