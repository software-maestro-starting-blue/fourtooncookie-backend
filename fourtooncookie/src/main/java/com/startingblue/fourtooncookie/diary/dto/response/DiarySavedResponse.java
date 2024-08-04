package com.startingblue.fourtooncookie.diary.dto.response;

import com.startingblue.fourtooncookie.diary.domain.Diary;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

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
                .characterId(diary.getCharacter().getId())
                .build();
    }
}