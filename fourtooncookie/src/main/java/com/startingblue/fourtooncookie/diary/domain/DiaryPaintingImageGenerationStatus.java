package com.startingblue.fourtooncookie.diary.domain;

public enum DiaryPaintingImageGenerationStatus {
    GENERATING,    // 이미지 생성 중 - 람다에 보내는 순간
    SUCCESS,       // 이미지 생성 성공
    FAILURE        // 이미지 생성 실패
}
