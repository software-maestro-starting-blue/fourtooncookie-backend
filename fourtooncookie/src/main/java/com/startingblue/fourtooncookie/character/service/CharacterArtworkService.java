package com.startingblue.fourtooncookie.character.service;

import com.startingblue.fourtooncookie.artwork.ArtworkService;
import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CharacterArtworkService {

    private final ArtworkService artworkService;

    public Artwork readById(Long artworkId) {
        return artworkService.readById(artworkId);
    }


    public Artwork getArtworkWithNameChange(Artwork artwork, Locale locale) {
        return artworkService.getArtworkWithNameChange(artwork, locale);
    }
}
