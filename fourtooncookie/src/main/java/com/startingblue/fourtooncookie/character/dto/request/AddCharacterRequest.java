package com.startingblue.fourtooncookie.character.dto.request;

import com.startingblue.fourtooncookie.character.domain.PaymentType;

import java.net.URL;

public record AddCharacterRequest(String characterVisionType, PaymentType paymentType, Long artworkId, String name, URL selectionThumbnailUrl, String basePrompt) {
}
