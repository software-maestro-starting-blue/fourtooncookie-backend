package com.startingblue.fourtooncookie.diary.domain;

import com.startingblue.fourtooncookie.config.BaseEntity;
import com.startingblue.fourtooncookie.character.domain.Character;

import com.startingblue.fourtooncookie.converter.jpa.LongListToStringConverter;
import com.startingblue.fourtooncookie.converter.jpa.UrlListToStringConverter;

import com.startingblue.fourtooncookie.validator.NotEmptyList;
import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @NotBlank(message = "일기 내용은 필수 입니다.")
    @Size(min = 1, max = 1000, message = "일기 내용은 1자 이상 1000자 이내여야 합니다.")
    private String content;

    private boolean isFavorite;

    @NotNull(message = "일기 날짜는 필수 입니다.")
    @Column(updatable = false)
    private LocalDate diaryDate;

    @NotEmptyList(message = "일기 그림 URL 목록은 최소 1개를 포함해야 합니다.")
    @Size(min = 1, max = 4, message = "일기 그림 URL 목록은 1개에서 4개 사이여야 합니다.")
    @Convert(converter = UrlListToStringConverter.class)
    @Builder.Default
    private List<URL> paintingImageUrls = new ArrayList<>();

    @NotEmptyList(message = "해시태그 ID 목록은 최소 1개를 포함해야 합니다.")
    @Size(min = 1, max = 4, message = "해시태그 ID 목록은 1개에서 4개 사이여야 합니다.")
    @Convert(converter = LongListToStringConverter.class)
    @Builder.Default
    private List<Long> hashtagsIds = new ArrayList<>();


    @NotNull(message = "일기에 그려질 캐릭터는 필수 입니다.")
    @ManyToOne(fetch = FetchType.LAZY)
    private Character character;

    @NotNull(message = "멤버 아이디는 필수 입니다.")
    private UUID memberId;

    public static DiaryBuilder builder() {
        return new CustomDiaryBuilder();
    }

    public void update(String content,
                       List<Long> hashtagIds,
                       Character character) {
        this.content = content;
        this.character = character;
        updateHashtags(hashtagIds);
        validate();
    }

    public void updateFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void updatePaintingImageUrls(List<URL> paintingImageUrls) {
        this.paintingImageUrls = new ArrayList<>(paintingImageUrls);
    }

    public void updateHashtags(List<Long> hashtagIds) {
        this.hashtagsIds = new ArrayList<>(hashtagIds);
    }

    public boolean isOwner(UUID memberId) {
        return this.memberId.equals(memberId);
    }

    private void validate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Diary>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private static class CustomDiaryBuilder extends DiaryBuilder {
        @Override
        public Diary build() {
            Diary diary = super.build();
            diary.validate();
            return diary;
        }
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
                Objects.equals(memberId, diary.memberId) &&
                Objects.equals(getCreatedDateTime(), diary.getCreatedDateTime()) &&
                Objects.equals(getModifiedDateTime(), diary.getModifiedDateTime());
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, content, isFavorite, diaryDate, paintingImageUrls, hashtagsIds, character, memberId, getCreatedDateTime(), getModifiedDateTime());
    }
}
