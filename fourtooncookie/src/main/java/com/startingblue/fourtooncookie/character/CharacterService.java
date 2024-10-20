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
    private final CharacterTranslationService characterTranslationService;

    public void createCharacter(final CharacterSaveRequest request) {
        CharacterVisionType visionType = findByCharacterVisionType(request.characterVisionType());
        Artwork artwork = characterArtworkService.readById(request.artworkId());

        verifyUniqueCharacter(request.name(), artwork, request.paymentType(), visionType);

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
    public List<Character> readAllCharacters() {
        return characterRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Character> readAllCharacters(Locale locale) {
        return readAllCharacters().stream()
                .map(character -> characterTranslationService.translateCharacter(character, locale))
                .toList();
    }

    public void updateCharacter(final Long characterId, final CharacterUpdateRequest request) {
        Character character = readById(characterId);
        Artwork artwork = characterArtworkService.readById(request.artworkId());

        character.update(request.characterVisionType(),
                request.paymentType(),
                artwork,
                request.name(),
                request.selectionThumbnailUrl(),
                request.basePrompt());
        characterRepository.save(character);
    }

    public void deleteCharacter(final Long characterId) {
        Character foundCharacter = readById(characterId);
        characterRepository.delete(foundCharacter);
    }

    @Transactional(readOnly = true)
    public Character readById(Long characterId) {
        return characterRepository.findById(characterId)
                .orElseThrow(() -> new CharacterNotFoundException("Character with ID " + characterId + " not found"));
    }

    @Transactional(readOnly = true)
    public Character readById(Long characterId, Locale locale) {
        Character foundCharacter = readById(characterId);
        return characterTranslationService.translateCharacter(foundCharacter, locale);
    }

    private CharacterVisionType findByCharacterVisionType(CharacterVisionType characterVisionType) {
        return CharacterVisionType.valueOf(characterVisionType.name());
    }

    private void verifyUniqueCharacter(String name, Artwork artwork, PaymentType paymentType, CharacterVisionType visionType) {
        boolean isDuplicate = characterRepository.existsByName(name) &&
                characterRepository.existsByArtwork(artwork) &&
                characterRepository.existsByPaymentType(paymentType) &&
                characterRepository.existsByCharacterVisionType(visionType);

        if (isDuplicate) {
            throw new CharacterDuplicateException("중복된 캐릭터입니다. 동일한 이름, 작품, 결제 유형, 캐릭터 비전 유형을 가진 캐릭터가 이미 존재합니다.");
        }
    }

}
