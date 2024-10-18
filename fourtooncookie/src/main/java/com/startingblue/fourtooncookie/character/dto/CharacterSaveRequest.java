package com.startingblue.fourtooncookie.character.dto;

import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.global.domain.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.net.URL;

public record CharacterSaveRequest(
        @NotNull(message = "캐릭터 비전 유형은 필수 입니다") CharacterVisionType characterVisionType,
        @NotNull(message = "결제 결제 유형은 필수 입니다.") PaymentType paymentType,
        @NotNull(message = "캐릭터 작품은 필수 입니다.") Long artworkId,
        @Size(min = 1, max = 255, message = "캐릭터 이름은 1자 이상 255자 이내여야 합니다.") String name,
        @NotNull(message = "캐릭터 선택 썸네일 URL은 필수 입니다.") URL selectionThumbnailUrl,
        @NotBlank(message = "캐릭터 기본 프롬프트는 필수 입니다.") String basePrompt) {
}
