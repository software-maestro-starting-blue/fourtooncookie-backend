package com.startingblue.fourtooncookie.diary.dto.request;

import com.startingblue.fourtooncookie.validator.NotEmptyList;
import jakarta.validation.constraints.Size;

import java.net.URL;
import java.util.List;

public record DiaryPaintingImagesUpdateRequest(
        @NotEmptyList(message = "일기 그림 URL 목록은 최소 1개를 포함해야 합니다.")
        @Size(max = 4, message = "일기 그림은 최대 4개 입니다.")
        List<URL> paintingImageUrls){
}
