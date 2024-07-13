package com.startingblue.fourtooncookie.diary.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DiarySavedResponse(
        String content,
        Boolean isFavorite,
        LocalDateTime diaryDate,
        List<String> paintingImageUrls,
        List<Long> hashtagIds
) {}
