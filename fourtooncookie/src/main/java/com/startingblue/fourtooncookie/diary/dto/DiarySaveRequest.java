package com.startingblue.fourtooncookie.diary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DiarySaveRequest(
        @NotBlank(message = "일기 내용은 필수 입니다.")
        @Size(min = 1, max = 1000, message = "일기 내용은 1자 이상 1000자 이내여야 합니다.") String content,

        @NotNull(message = "일기 날짜는 필수 입니다.") LocalDate diaryDate,

        @NotNull(message = "일기에 그려질 캐릭터 ID는 필수 입니다.") Long characterId) {
}