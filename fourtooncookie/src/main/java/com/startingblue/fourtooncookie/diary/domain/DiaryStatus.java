package com.startingblue.fourtooncookie.diary.domain;

import lombok.Getter;

@Getter
public enum DiaryStatus {
    IN_PROGRESS("생성 중"),
    COMPLETED("완료"),
    FAILED("생성 실패");

    private final String description;

    DiaryStatus(String description) {
        this.description = description;
    }
}
