package com.startingblue.fourtooncookie.paintingimage.domain;

import com.startingblue.fourtooncookie.diary.domain.Diary;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaintingImage {

    @Id
    @GeneratedValue
    @Column(name = "painting_image_id")
    private Long id;

    private String imageUrl;

    @ManyToOne
    private Diary diary;

    private Integer gridPosition;
}
