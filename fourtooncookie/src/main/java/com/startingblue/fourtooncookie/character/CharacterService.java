package com.startingblue.fourtooncookie.character;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.character.domain.PaymentType;
import com.startingblue.fourtooncookie.character.dto.CharacterSaveRequest;
import com.startingblue.fourtooncookie.character.dto.CharacterUpdateRequest;
import com.startingblue.fourtooncookie.character.exception.CharacterDuplicateException;
import com.startingblue.fourtooncookie.character.exception.CharacterNotFoundException;
import com.startingblue.fourtooncookie.character.service.CharacterArtworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RequiredArgsConstructor
@Transactional
@Service
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final CharacterArtworkService characterArtworkService;
    private final MessageSource xmlMessageSource;

    public void addCharacter(final CharacterSaveRequest request) {
        CharacterVisionType visionType = findByCharacterVisionType(request.characterVisionType());
        Artwork artwork = characterArtworkService.readById(request.artworkId());

        validateUniqueCharacter(request.name(), artwork, request.paymentType(), visionType);

        characterRepository.save(Character.builder()
                .characterVisionType(visionType)
                .paymentType(request.paymentType())
                .name(request.name())
                .artwork(artwork)
                .selectionThumbnailUrl(request.selectionThumbnailUrl())
                .basePrompt(request.basePrompt())
                .build());
    }

    @Transactional(readOnly = true)
    public Character getById(Long characterId) {
        return characterRepository.findById(characterId)
                .orElseThrow(() -> new CharacterNotFoundException("Character with ID " + characterId + " not found"));
    }

    @Transactional(readOnly = true)
    public Character getById(Long characterId, Locale locale) {
        Character foundCharacter = getById(characterId);
        return localizeCharacter(foundCharacter, locale);
    }


    @Transactional(readOnly = true)
    public List<Character> getAllCharacters() {
        return characterRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Character> getAllCharacters(Locale locale) {
        return getAllCharacters().stream()
                .map(character -> localizeCharacter(character, locale))
                .toList();
    }

    public void modifyCharacter(final Long characterId, final CharacterUpdateRequest request) {
        Character character = getById(characterId);
        Artwork artwork = characterArtworkService.readById(request.artworkId());

        character.update(request.characterVisionType(),
                request.paymentType(),
                artwork,
                request.name(),
                request.selectionThumbnailUrl(),
                request.basePrompt());
        characterRepository.save(character);
    }

    public void removeCharacter(final Long characterId) {
        Character foundCharacter = getById(characterId);
        characterRepository.delete(foundCharacter);
    }

    private Character localizeCharacter(Character character, Locale locale) {
        Artwork localizedArtwork = characterArtworkService.getArtworkWithNameChange(character.getArtwork(), locale);

        String localizedCharacterName = getLocalizedCharacterName(character.getId(), locale);
        return getCharacterWithNameChangeAndArtworkChange(character, localizedCharacterName, localizedArtwork);
    }

    private Character getCharacterWithNameChangeAndArtworkChange(Character character, String localizedName, Artwork localizedArtwork ) {
        return Character.builder()
                .id(character.getId())
                .characterVisionType(character.getCharacterVisionType())
                .paymentType(character.getPaymentType())
                .name(localizedName)
                .artwork(localizedArtwork)
                .selectionThumbnailUrl(character.getSelectionThumbnailUrl())
                .basePrompt(character.getBasePrompt())
                .build();
    }

    public String getLocalizedCharacterName(Long characterId, Locale locale) {
        return Objects.requireNonNull(xmlMessageSource.getMessage("character.name." + characterId, null, locale));
    }

    private CharacterVisionType findByCharacterVisionType(CharacterVisionType characterVisionType) {
        return CharacterVisionType.valueOf(characterVisionType.name());
    }

    private void validateUniqueCharacter(String name, Artwork artwork, PaymentType paymentType, CharacterVisionType visionType) {
        boolean isDuplicate = characterRepository.existsByName(name) &&
                characterRepository.existsByArtwork(artwork) &&
                characterRepository.existsByPaymentType(paymentType) &&
                characterRepository.existsByCharacterVisionType(visionType);

        if (isDuplicate) {
            throw new CharacterDuplicateException("Duplicate character. A character with the same name, work, payment type, and character vision type already exists.");
        }
    }

}
