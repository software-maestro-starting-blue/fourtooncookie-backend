package com.startingblue.fourtooncookie.artwork.service;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.translation.TranslationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@AllArgsConstructor
public class ArtworkTranslationService {

    private final TranslationService translationService;

    @Transactional(readOnly = true)
    public Artwork translateArtwork(Artwork artwork, Locale locale) {
        return translationService.getTranslatedObject(artwork, locale);
    }

}
