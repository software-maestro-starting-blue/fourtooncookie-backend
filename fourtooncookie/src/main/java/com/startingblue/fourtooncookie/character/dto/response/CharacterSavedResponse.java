package com.startingblue.fourtooncookie.character.dto.response;

import lombok.Builder;

import java.net.URL;

@Builder
public record CharacterSavedResponse(Long id,
                                     String paymentType,
                                     String artworkTitle,
                                     URL artworkThumbnailUrl,
                                     String name,
                                     URL selectionThumbnailUrl) {
}