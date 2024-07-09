package com.startingblue.fourtooncookie.diary.dto.response;

import java.util.List;

public record DiarySavedResponse(String Character, String content, String thumbnailUrl, List<String> diaryHashtags) {
}
