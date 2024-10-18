package com.startingblue.fourtooncookie.diary.dto;

import com.startingblue.fourtooncookie.diary.domain.Diary;

import java.util.List;

public record DiarySavedResponses(List<DiarySavedResponse> diarySavedResponses) {

    public static DiarySavedResponses of(List<Diary> diaries) {
        return new DiarySavedResponses(diaries.stream()
                .map(DiarySavedResponse::of)
                .toList());
    }
}
