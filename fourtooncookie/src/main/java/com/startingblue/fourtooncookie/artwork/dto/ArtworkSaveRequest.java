package com.startingblue.fourtooncookie.artwork.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.net.URL;

public record ArtworkSaveRequest(
        @NotBlank(message = "작품명은 필수 입니다.")
        @Size(min = 1, max = 255, message = "작품명의 글자수는 1에서 255자 이내여야 합니다.") String title,
        @NotNull(message = "작품 썸네일 URL이 존재해야 합니다.") URL thumbnailUrl) {
}
