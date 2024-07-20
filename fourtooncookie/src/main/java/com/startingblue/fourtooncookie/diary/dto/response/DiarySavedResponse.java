package com.startingblue.fourtooncookie.diary.dto.response;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Builder
public record DiarySavedResponse(
        Long diaryId,
        String content,
        boolean isFavorite,
        LocalDate diaryDate,
        List<String> paintingImageUrls,
        List<Long> hashtagIds,
        Long characterId
) {
    public static DiarySavedResponse of(Diary diary) {
        return DiarySavedResponse.builder()
                .diaryId(diary.getId())
                .content(diary.getContent())
                .isFavorite(diary.isFavorite())
                .diaryDate(diary.getDiaryDate())
                .paintingImageUrls(diary.getPaintingImageUrls()
                        .stream()
                        .map(String::valueOf)
                        .toList())
                .hashtagIds(diary.getHashtagsIds())
                .characterId(Optional.ofNullable(diary.getCharacter())
                        .map(Character::getId)
                        .orElse(null))
                .build();
    }
}