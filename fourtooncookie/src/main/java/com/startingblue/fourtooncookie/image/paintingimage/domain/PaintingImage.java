package com.startingblue.fourtooncookie.image.paintingimage.domain;

import com.startingblue.fourtooncookie.character.domain.ModelType;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.image.domain.Image;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaintingImage extends Image {

    protected static final String DEFAULT_IMAGE_PATH = "";
    protected static final int WIDTH_SIZE = 512;
    protected static final int HEIGHT_SIZE = 512;

    @Id @GeneratedValue
    @Column(name = "painting_image_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ModelType modelType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Diary diary;

    private Integer gridPosition;

    protected PaintingImage(String path, ModelType modelType, Diary diary, Integer gridPosition) {
        super(path, WIDTH_SIZE, HEIGHT_SIZE);
        this.modelType = modelType;
        this.diary = diary;
        this.gridPosition = gridPosition;
    }
}
