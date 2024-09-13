package com.startingblue.fourtooncookie.diary.dto.response;

import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryStatus;
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
        Long characterId,
        DiaryStatus diaryStatus
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
                .characterId(diary.getCharacter().getId())
                .diaryStatus(diary.getStatus())
                .build();
    }
}