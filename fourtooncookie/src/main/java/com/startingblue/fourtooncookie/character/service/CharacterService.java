package com.startingblue.fourtooncookie.character.service;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.service.ArtworkService;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.ModelType;
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
        final ModelType modelType = ModelType.valueOf(request.modelType());
        final Artwork artwork = artworkService.findById(request.artworkId());
        final Character character = new Character(
                modelType,
                artwork,
                request.name(),
                request.selectionThumbnailUrl(),
                request.basePrompt()
        );

        characterRepository.save(character);
    }

    public void modifyCharacter(final Long characterId, final ModifyCharacterRequest request) {
        final Character character = characterRepository
                .findById(characterId)
                .orElseThrow(CharacterNoSuchElementException::new);
        final ModelType modelType = ModelType.valueOf(request.modelType());
        final Artwork artwork = artworkService.findById(request.artworkId());

        character.update(modelType,
                artwork,
                request.name(),
                request.selectionThumbnailUrl(),
                request.basePrompt());

        characterRepository.save(character);
    }

    public void deleteCharacter(final Long characterId) {
        characterRepository.deleteById(characterId);
    }

    @Transactional(readOnly = true)
    public CharacterResponses showCharacters() {
        final List<Character> characters = characterRepository.findAll();

        return new CharacterResponses(characters.stream()
                .map(character -> new CharacterResponse(
                        character.getId(),
                        character.getModelType().name(),
                        character.getName(),
                        character.getSelectionThumbnailUrl()))
                .toList());
    }

    public Character findById(Long characterId) {
        return characterRepository.findById(characterId)
                        .orElseThrow(CharacterNoSuchElementException::new);
    }
}
