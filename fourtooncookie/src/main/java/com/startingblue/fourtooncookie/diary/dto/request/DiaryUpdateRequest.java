package com.startingblue.fourtooncookie.diary.dto.request;

import java.util.List;

public record DiaryUpdateRequest(String content, List<Long> hashtagIds, Long characterId) {
}
