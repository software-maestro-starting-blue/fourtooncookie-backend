package com.startingblue.fourtooncookie.diary.dto.response;

import java.util.List;

public record DiarySavedResponse(String content, Boolean isFavorite, String createdAt, String updatedAt, List<String> paintingImageUrls, List<Long> hashtagIds) {
}
