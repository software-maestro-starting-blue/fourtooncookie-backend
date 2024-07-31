package com.startingblue.fourtooncookie.diary.dto.request;

import com.startingblue.fourtooncookie.validator.NotEmptyList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record DiarySaveRequest(
        @NotBlank(message = "일기 내용은 필수 입니다.")
        @Size(min = 1, max = 1000, message = "일기 내용은 1자 이상 1000자 이내여야 합니다.") String content,

        @NotNull(message = "일기 날짜는 필수 입니다.") LocalDate diaryDate,

        @NotEmptyList(message = "해시태그 ID 목록은 최소 1개를 포함해야 합니다.")
        @Size(min = 1, max = 4, message = "해시태그 ID 목록은 1개에서 4개 사이여야 합니다.")
        List<Long> hashtagIds,

        @NotNull(message = "일기에 그려질 캐릭터 ID는 필수 입니다.") Long characterId) {
}