package com.startingblue.fourtooncookie.artwork.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

@Entity
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artwork_id")
    private Long id;

    @NotBlank(message = "작품명은 비워둘 수 없습니다.")
    @Size(min = 1, max = 255, message = "작품명의 글자수는 1에서 255자 이내여야 합니다.")
    private String title;

    @NotNull(message = "작품 썸네일이 존재해야 합니다.")
    @Column(name = "thumbnail_url", nullable = false)
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
