package com.startingblue.fourtooncookie.artwork.dto.response;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;

import java.util.List;

public record ArtworkSavedResponses(List<ArtworkSavedResponse> artworks) {

    public static ArtworkSavedResponses of(List<Artwork> artworks) {
        return new ArtworkSavedResponses(artworks.stream()
                .map(ArtworkSavedResponse::of)
                .toList());
    }
}
