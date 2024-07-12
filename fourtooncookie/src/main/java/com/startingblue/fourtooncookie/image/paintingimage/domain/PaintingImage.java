package com.startingblue.fourtooncookie.image.paintingimage.domain;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    private Integer gridPosition;

    public PaintingImage(String path, Diary diary, Integer gridPosition) {
        super(path, WIDTH_SIZE, HEIGHT_SIZE);
        this.diary = diary;
        this.gridPosition = gridPosition;
    }

    public void assignDiary(Diary diary) {
        if (this.diary != null) {
            this.diary.getPaintingImages().remove(this);
        }
        this.diary = diary;
        if (diary != null) {
            if (diary.getPaintingImages()
                    .stream()
                    .anyMatch(img -> img.getGridPosition().equals(this.gridPosition))) {
                throw new IllegalArgumentException("이미 존재하는 그리드 입니다.");
            }
            diary.getPaintingImages().add(this);
        }
    }
}
