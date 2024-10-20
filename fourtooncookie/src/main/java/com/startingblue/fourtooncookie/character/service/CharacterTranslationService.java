package com.startingblue.fourtooncookie.character.service;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.translation.TranslationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@AllArgsConstructor
public class CharacterTranslationService {

    private final TranslationService translationService;

    public Character translateCharacter(Character character, Locale locale) {
        Artwork localizedArtwork = translationService.getTranslatedObject(character.getArtwork().clone(), locale);

        Character clonedCharacter = character.toBuilder()
                .artwork(localizedArtwork)
                .build();

        return translationService.getTranslatedObject(clonedCharacter, locale);
    }

}