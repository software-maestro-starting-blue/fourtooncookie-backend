package com.startingblue.fourtooncookie.character.dto.response;

import com.startingblue.fourtooncookie.character.domain.Character;

import java.util.List;

public record CharacterSavedResponses(List<CharacterSavedResponse> characterSavedResponses) {

    public static CharacterSavedResponses of(List<Character> characters) {
        return new CharacterSavedResponses(characters.stream()
                .map(character -> CharacterSavedResponse.builder()
                        .id(character.getId())
                        .paymentType(character.getPaymentType().name())
                        .artworkThumbnailUrl(character.getArtwork().getThumbnailUrl())
                        .artworkTitle(character.getArtwork().getTitle())
                        .name(character.getName())
                        .selectionThumbnailUrl(character.getSelectionThumbnailUrl())
                        .build())
                .toList());
    }
}
