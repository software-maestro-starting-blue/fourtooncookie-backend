package com.startingblue.fourtooncookie.character.dto.response;

import com.startingblue.fourtooncookie.character.domain.Character;
import lombok.Builder;

import java.net.URL;

@Builder
public record CharacterSavedResponse(Long id,
                                     String paymentType,
                                     String artworkTitle,
                                     URL artworkThumbnailUrl,
                                     String name,
                                     URL selectionThumbnailUrl) {
    public static CharacterSavedResponse of(Character character) {
        return new CharacterSavedResponseBuilder()
                .id(character.getId())
                .paymentType(character.getPaymentType().toString())
                .artworkTitle(character.getArtwork().getTitle())
                .artworkThumbnailUrl(character.getArtwork().getThumbnailUrl())
                .name(character.getName())
                .selectionThumbnailUrl(character.getSelectionThumbnailUrl())
                .build();
    }
}