package com.startingblue.fourtooncookie.character.service;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.service.ArtworkService;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.character.dto.request.AddCharacterRequest;
import com.startingblue.fourtooncookie.character.dto.request.ModifyCharacterRequest;
import com.startingblue.fourtooncookie.character.dto.response.CharacterResponse;
import com.startingblue.fourtooncookie.character.dto.response.CharacterResponses;
import com.startingblue.fourtooncookie.character.exception.CharacterNoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final ArtworkService artworkService;

    public void addCharacter(final AddCharacterRequest request) {
        CharacterVisionType visionType = CharacterVisionType.valueOf(request.characterVisionType());
        Artwork artwork = artworkService.readById(request.artworkId());

        Character character = Character.builder()
                .characterVisionType(visionType)
                .paymentType(request.paymentType())
                .name(request.name())
                .artwork(artwork)
                .selectionThumbnailUrl(request.selectionThumbnailUrl())
                .basePrompt(request.basePrompt())
                .build();
        characterRepository.save(character);
    }

    public void modifyCharacter(final Long characterId, final ModifyCharacterRequest request) {
        Character character = findById(characterId);
        CharacterVisionType visionType = CharacterVisionType.valueOf(request.characterVisionType());
        Artwork artwork = artworkService.readById(request.artworkId());

        character.update(visionType,
                request.paymentType(),
                artwork,
                request.name(),
                request.selectionThumbnailUrl(),
                request.basePrompt());
        characterRepository.save(character);
    }

    public void deleteCharacter(final Long characterId) {
        Character foundCharacter = findById(characterId);
        characterRepository.delete(foundCharacter);
    }

    @Transactional(readOnly = true)
    public CharacterResponses showCharacters() {
        final List<Character> characters = characterRepository.findAll();

        return new CharacterResponses(characters.stream()
                .map(character -> CharacterResponse.builder()
                        .id(character.getId())
                        .paymentType(character.getPaymentType().name())
                        .artworkThumbnailUrl(character.getArtwork().getThumbnailUrl())
                        .artworkTitle(character.getArtwork().getTitle())
                        .name(character.getName())
                        .selectionThumbnailUrl(character.getSelectionThumbnailUrl())
                        .build())
                .toList());
    }

    public Character findById(Long characterId) {
        return characterRepository.findById(characterId)
                        .orElseThrow(CharacterNoSuchElementException::new);
    }
}
