package com.startingblue.fourtooncookie.diary.domain;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.DiaryHashtag;
import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.image.paintingimage.domain.PaintingImage;
import com.startingblue.fourtooncookie.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Slf4j
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    private String content;

    private Boolean isFavorite;

    private LocalDateTime diaryDate;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PaintingImage> paintingImages;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DiaryHashtag> hashtags;

    @ManyToOne(fetch = FetchType.LAZY)
    private Character character;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public void update(String content, LocalDateTime modifiedAt, List<Hashtag> hashtags, Character character) {
        this.content = content;
        this.modifiedAt = modifiedAt;
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

    public void updateHashtags(List<Hashtag> hashtags) {
        if (hasHashtags()) {
            removeHashtags(hashtags);
        }
        addHashtags(hashtags);
    }

    private void addPaintingImage(PaintingImage paintingImage) {
        paintingImages.add(paintingImage);
        paintingImage.assignDiary(this);
    }

    private void removePaintingImages() {
        for (PaintingImage paintingImage : paintingImages) {
            paintingImage.assignDiary(null);
        }
        paintingImages.clear();
    }

    private boolean hasPaintingImages() {
        return paintingImages != null && !paintingImages.isEmpty();
    }

    private void removeHashtags(List<Hashtag> hashtags) {
        for (Hashtag hashtag : hashtags) {
            removeHashtag(hashtag);
        }
    }

    private void removeHashtag(Hashtag hashtag) {
        DiaryHashtag diaryHashtag = new DiaryHashtag(this, hashtag);
        hashtags.remove(diaryHashtag);
        diaryHashtag.assignDiary(null);
    }

    private void addHashtags(List<Hashtag> hashtags) {
        for (Hashtag hashtag : hashtags) {
            addHashtag(hashtag);
        }
    }

    private void addHashtag(Hashtag hashtag) {
        DiaryHashtag diaryHashtag = new DiaryHashtag(this, hashtag);
        hashtags.add(diaryHashtag);
    }


    private boolean hasHashtags() {
        return hashtags != null && !hashtags.isEmpty();
    }
}
