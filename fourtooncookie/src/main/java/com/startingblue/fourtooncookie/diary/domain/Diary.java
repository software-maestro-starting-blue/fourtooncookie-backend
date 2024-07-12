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

    public Diary(String content, LocalDateTime createdAt, Character character, Member member) {
        this.content = content;
        this.isFavorite = false;
        this.character = character;
        this.member = member;
        this.createdAt = createdAt;
        this.modifiedAt = createdAt;
    }

    public void addHashtag(Hashtag hashtag) {
        DiaryHashtag diaryHashtag = new DiaryHashtag(this, hashtag);
        hashtags.add(diaryHashtag);
    }

    public void addHashtags(List<Hashtag> hashtags) {
        for (Hashtag hashtag : hashtags) {
            addHashtag(hashtag);
        }
    }

    private void removeHashtag(Hashtag hashtag) {
        DiaryHashtag diaryHashtag = new DiaryHashtag(this, hashtag);
        hashtags.remove(diaryHashtag);
        diaryHashtag.assignDiary(null);
    }

    public void removeHashtags(List<Hashtag> hashtags) {
        for (Hashtag hashtag : hashtags) {
            removeHashtag(hashtag);
        }
    }

    public void addPaintingImage(PaintingImage paintingImage) {
        paintingImages.add(paintingImage);
        paintingImage.assignDiary(this);
    }

    public void removePaintingImage(PaintingImage paintingImage) {
        paintingImages.remove(paintingImage);
        paintingImage.assignDiary(null);
    }

    public void update(String content, boolean isFavorite, List<Hashtag> hashtags, LocalDateTime modifiedAt, Character character) {
        this.content = content;
        this.isFavorite = isFavorite;
        removeHashtags(hashtags);
        addHashtags(hashtags);
        this.modifiedAt = modifiedAt;
        this.character = character;
    }
}
