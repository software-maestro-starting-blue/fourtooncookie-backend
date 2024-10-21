package com.startingblue.fourtooncookie.character.service;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.translation.TranslationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@AllArgsConstructor
public class CharacterTranslationService {

    private final TranslationService translationService;

    @Transactional(readOnly = true)
    public Character translateCharacter(Character character, Locale locale) {
        Artwork localizedArtwork = translationService.getTranslatedObject(character.getArtwork(), locale);

        Character clonedCharacter = character.toBuilder()
                .artwork(localizedArtwork)
                .build();

        return translationService.getTranslatedObject(clonedCharacter, locale);
    }

}
