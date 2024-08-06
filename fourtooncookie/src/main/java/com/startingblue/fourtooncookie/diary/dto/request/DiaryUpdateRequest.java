package com.startingblue.fourtooncookie.diary.dto.request;

import com.startingblue.fourtooncookie.validator.NotEmptyList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DiaryUpdateRequest(
        @NotBlank(message = "일기 내용은 필수 입니다.")
        @Size(min = 1, max = 1000, message = "일기 내용은 1자 이상 1000자 이내여야 합니다.")
        String content,

        @NotEmptyList(message = "해시태그 ID 목록은 최소 1개를 포함해야 합니다.")
        @Size(min = 1, message = "해시태그 ID 목록은 최소 1개를 포함해야 합니다.")
        List<Long> hashtagIds,

        @NotNull(message = "일기에 그려질 캐릭터 ID는 필수 입니다.") Long characterId) {
}
