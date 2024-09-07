package com.startingblue.fourtooncookie.diary.dto.request;

import jakarta.validation.constraints.Size;

import java.net.URL;
import java.util.List;

public record DiaryPaintingImagesUpdateRequest(
        @Size(max = 4, message = "일기 그림은 최대 4개 입니다.")
        List<URL> paintingImageUrls){
}
