package com.startingblue.fourtooncookie.diary.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public record DiarySaveRequest(String content, LocalDateTime diaryDate, List<Long> hashtagIds) {
}
