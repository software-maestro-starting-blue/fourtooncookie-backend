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
import com.startingblue.fourtooncookie.character.service.CharacterTranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Transactional
@Service
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final CharacterArtworkService characterArtworkService;
    private final CharacterTranslationService characterTranslationService;

    public void addCharacter(final CharacterSaveRequest request) {
        CharacterVisionType visionType = getCharacterVisionType(request.characterVisionType());
        Artwork artwork = characterArtworkService.getById(request.artworkId());

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
        return characterTranslationService.translateCharacter(foundCharacter, locale);
    }


    @Transactional(readOnly = true)
    public List<Character> getAllCharacters() {
        return characterRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Character> getAllCharacters(Locale locale) {
        return getAllCharacters().stream()
                .map(character -> characterTranslationService.translateCharacter(character, locale))
                .toList();
    }

    public void modifyCharacter(final Long characterId, final CharacterUpdateRequest request) {
        Character character = getById(characterId);
        Artwork artwork = characterArtworkService.getById(request.artworkId());

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

    private CharacterVisionType getCharacterVisionType(CharacterVisionType characterVisionType) {
        return CharacterVisionType.valueOf(characterVisionType.name());
    }

    private void validateUniqueCharacter(String name, Artwork artwork, PaymentType paymentType, CharacterVisionType visionType) {
        if (characterRepository.existsByName(name) &&
                characterRepository.existsByArtwork(artwork) &&
                characterRepository.existsByPaymentType(paymentType) &&
                characterRepository.existsByCharacterVisionType(visionType)) {
            throw new CharacterDuplicateException("Duplicate character. A character with the same name, work, payment type, and character vision type already exists.");
        }
    }

}
