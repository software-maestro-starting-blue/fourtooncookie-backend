package com.startingblue.fourtooncookie.artwork.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.net.URL;

public record ArtworkSaveRequest(
        @Size(min = 1, max = 255, message =  "작품명은 비워둘 수 없습니다.") String title,
        @NotNull(message = "작품 썸네일 URL이 존재해야 합니다.") URL thumbnailUrl) {
}
