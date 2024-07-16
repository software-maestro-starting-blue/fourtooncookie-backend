package com.startingblue.fourtooncookie.diary.dto.response;

import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.image.paintingimage.domain.PaintingImage;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DiarySavedResponse(
        Long diaryId,
        String content,
        Boolean isFavorite,
        LocalDateTime diaryDate,
        List<String> paintingImageUrls,
        List<Long> hashtagIds,
        Long characterId
) {
    public static DiarySavedResponse of(Diary diary) {
        return DiarySavedResponse.builder()
                .diaryId(diary.getId())
                .content(diary.getContent())
                .isFavorite(diary.getIsFavorite())
                .diaryDate(diary.getDiaryDate())
                .paintingImageUrls(diary.getPaintingImages()
                        .stream()
                        .map(PaintingImage::getPath)
                        .toList())
                .hashtagIds(diary.getHashtags())
                .characterId(1L) // todo: 임시 데이터
                .build();
    }
}