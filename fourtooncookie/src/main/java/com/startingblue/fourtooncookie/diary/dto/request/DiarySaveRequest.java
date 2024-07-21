package com.startingblue.fourtooncookie.diary.dto.request;

import java.time.LocalDate;
import java.util.List;

public record DiarySaveRequest(String content, LocalDate diaryDate, List<Long> hashtagIds, Long characterId) {
}