package com.startingblue.fourtooncookie.diary.domain;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.converter.LongListToStringConverter;
import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.image.paintingimage.domain.PaintingImage;
import com.startingblue.fourtooncookie.member.domain.Member;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Slf4j
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @Size(min = 1, max = 1000)
    @NotBlank
    private String content;

    private boolean isFavorite;

    private LocalDate diaryDate;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Nullable
    private List<PaintingImage> paintingImages;

    @Convert(converter = LongListToStringConverter.class)
    @Builder.Default
    private List<Long> hashtagsIds= new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
//    @NotNull TODO
    private Character character;

    @ManyToOne(fetch = FetchType.LAZY)
//    @NotNull TODO
    private Member member;

    @Builder
    public Diary(String content, boolean isFavorite,
                 LocalDate diaryDate, LocalDateTime createdAt, LocalDateTime modifiedAt,
                 @Nullable List<PaintingImage> paintingImages, List<Long> hashtagsIds,
                 Character character, Member member) {
        this.content = content;
        this.isFavorite = isFavorite;
        this.diaryDate = diaryDate;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.paintingImages = paintingImages;
        this.hashtagsIds = hashtagsIds;
        this.character = character;
        this.member = member;
    }

    public void update(String content, LocalDateTime modifiedAt,
                       List<Hashtag> hashtags, Character character) {
        this.content = content;
        this.modifiedAt = modifiedAt;
        this.paintingImages = new ArrayList<>();
        this.character = character;
        updateHashtags(hashtags);
    }

    public void updatePaintingImages(List<PaintingImage> paintingImages) {
        if (hasPaintingImages()) {
            removePaintingImages();
        }
        for (PaintingImage paintingImage : paintingImages) {
            addPaintingImage(paintingImage);
        }
    }

    private boolean hasPaintingImages() {
        return paintingImages != null && !paintingImages.isEmpty();
    }

    private void removePaintingImages() {
        for (PaintingImage paintingImage : Objects.requireNonNull(paintingImages)) {
            paintingImage.assignDiary(null);
        }
        paintingImages.clear();
    }

    private void addPaintingImage(PaintingImage paintingImage) {
        paintingImages.add(paintingImage);
        paintingImage.assignDiary(this);
    }

    public void updateHashtags(List<Hashtag> hashtags) {
        this.hashtagsIds = hashtags.stream()
                .map(Hashtag::getId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Diary diary = (Diary) o;
        return isFavorite == diary.isFavorite && Objects.equals(id, diary.id) && Objects.equals(content, diary.content) && Objects.equals(diaryDate, diary.diaryDate) && Objects.equals(createdAt, diary.createdAt) && Objects.equals(modifiedAt, diary.modifiedAt) && Objects.equals(paintingImages, diary.paintingImages) && Objects.equals(hashtagsIds, diary.hashtagsIds) && Objects.equals(character, diary.character) && Objects.equals(member, diary.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, isFavorite, diaryDate, createdAt, modifiedAt, paintingImages, hashtagsIds, character, member);
    }
}