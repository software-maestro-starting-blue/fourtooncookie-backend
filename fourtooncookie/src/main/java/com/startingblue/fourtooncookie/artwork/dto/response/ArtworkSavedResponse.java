package com.startingblue.fourtooncookie.artwork.dto.response;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;

import java.net.URL;

public record ArtworkSavedResponse(String title, URL thumbnailUrl) {

    public static ArtworkSavedResponse of(Artwork artwork) {
        return new ArtworkSavedResponse(artwork.getTitle(), artwork.getThumbnailUrl());
    }
}
