package com.startingblue.fourtooncookie.diary.dto.response;

import java.util.List;

public record DiarySavedResponse(String content, Boolean isFavorite, List<String> paintingImageUrls, List<Long> hashtagIds, String modifiedAt) {
}
