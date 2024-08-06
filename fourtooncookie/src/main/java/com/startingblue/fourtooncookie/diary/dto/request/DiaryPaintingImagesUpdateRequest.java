package com.startingblue.fourtooncookie.diary.dto.request;

import com.startingblue.fourtooncookie.validator.NotEmptyList;
import jakarta.validation.constraints.Size;

import java.net.URL;
import java.util.List;

public record DiaryPaintingImagesUpdateRequest(
        @NotEmptyList(message = "일기 그림 URL 목록은 최소 1개를 포함해야 합니다.")
        @Size(min = 1, max = 4, message = "일기 그림 URL 목록은 1개에서 4개 사이여야 합니다.")
        List<URL> paintingImageUrls){
}
