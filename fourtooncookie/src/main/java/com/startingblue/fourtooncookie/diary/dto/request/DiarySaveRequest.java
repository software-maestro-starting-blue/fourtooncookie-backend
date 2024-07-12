package com.startingblue.fourtooncookie.diary.dto.request;

import java.util.List;

public record DiarySaveRequest(String content, List<Long> hashtagIds) {
}
