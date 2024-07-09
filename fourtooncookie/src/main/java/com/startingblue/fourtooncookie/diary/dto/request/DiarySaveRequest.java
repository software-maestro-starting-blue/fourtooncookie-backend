package com.startingblue.fourtooncookie.diary.dto.request;

import java.util.Set;

public record DiarySaveRequest(Long memberId, Long characterId, String content, String thumbnailUrl, Set<Long> hashtagIds) {
}
