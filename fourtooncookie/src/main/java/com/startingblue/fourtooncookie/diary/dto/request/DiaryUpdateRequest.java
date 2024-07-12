package com.startingblue.fourtooncookie.diary.dto.request;

import java.util.List;

public record DiaryUpdateRequest(String content, boolean isFavorite, List<Long> hashtagIds, String modifiedAt, Long characterId) {
}
