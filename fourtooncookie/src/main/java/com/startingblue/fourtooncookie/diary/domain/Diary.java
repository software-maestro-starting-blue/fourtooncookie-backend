package com.startingblue.fourtooncookie.diary.domain;

import com.startingblue.fourtooncookie.BaseEntity;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.converter.LongListToStringConverter;
import com.startingblue.fourtooncookie.converter.UrlListToStringConverter;
import com.startingblue.fourtooncookie.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Slf4j
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @Size(min = 1, max = 1000)
    @NotBlank
    private String content;

    private boolean isFavorite;

    private LocalDate diaryDate;

    @Convert(converter = UrlListToStringConverter.class)
    @Builder.Default
    private List<URL> paintingImageUrls = new ArrayList<>();

    @Convert(converter = LongListToStringConverter.class)
    @Builder.Default
    private List<Long> hashtagsIds= new ArrayList<>();

    //    @NotNull TODO
    @ManyToOne(fetch = FetchType.LAZY)
    private Character character;

    //    @NotNull TODO
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public void update(String content,
                       List<URL> paintingImageUrls,
                       List<Long> hashtagIds,
                       Character character) {
        this.content = content;
        this.character = character;
        updatePaintingImageUrls(paintingImageUrls);
        updateHashtags(hashtagIds);
    }

    public void updatePaintingImageUrls(List<URL> paintingImageUrls) {
        this.paintingImageUrls = new ArrayList<>(paintingImageUrls);
    }

    public void updateHashtags(List<Long> hashtagIds) {
        this.hashtagsIds = new ArrayList<>(hashtagIds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Diary diary = (Diary) o;
        return isFavorite == diary.isFavorite &&
                Objects.equals(id, diary.id) &&
                Objects.equals(content, diary.content) &&
                Objects.equals(diaryDate, diary.diaryDate) &&
                Objects.equals(paintingImageUrls, diary.paintingImageUrls) &&
                Objects.equals(hashtagsIds, diary.hashtagsIds) &&
                Objects.equals(character, diary.character) &&
                Objects.equals(member, diary.member);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, content, isFavorite, diaryDate, paintingImageUrls, hashtagsIds, character, member);
    }
}