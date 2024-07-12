package com.startingblue.fourtooncookie.diary.domain;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.DiaryHashtag;
import com.startingblue.fourtooncookie.hashtag.domain.Hashtag;
import com.startingblue.fourtooncookie.image.paintingimage.domain.PaintingImage;
import com.startingblue.fourtooncookie.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    private String content;

    private Boolean isFavorite;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PaintingImage> paintingImages = new ArrayList<>();

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DiaryHashtag> hashtags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Character character;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public Diary(String content, LocalDateTime createdAt, List<Hashtag> hashtags, Character character, Member member) {
        this.content = content;
        this.isFavorite = false;
        updateHashtags(hashtags);
        this.character = character;
        this.member = member;
        this.createdAt = createdAt;
        this.modifiedAt = createdAt;
    }

    public void update(String content, boolean isFavorite, LocalDateTime modifiedAt, List<Hashtag> hashtags, Character character) {
        this.content = content;
        this.isFavorite = isFavorite;
        this.modifiedAt = modifiedAt;
        updateHashtags(hashtags);
        this.character = character;
    }

    private void updatePaintingImages(List<PaintingImage> paintingImages) {
        removePaintingImages();
        for (PaintingImage paintingImage : paintingImages) {
            addPaintingImage(paintingImage);
        }
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

    private void updateHashtags(List<Hashtag> hashtags) {
        removeHashtags(hashtags);
        addHashtags(hashtags);
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

}
