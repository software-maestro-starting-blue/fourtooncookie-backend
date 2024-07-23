package com.startingblue.fourtooncookie.character.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.CharacterType;
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

    public void addCharacter(final AddCharacterRequest request) {
        final CharacterType characterType = CharacterType.valueOf(request.modelType());
        final Character character = new Character(
                characterType,
                request.name(),
                request.selectionThumbnailUrl()
        );

        characterRepository.save(character);
    }

    public void modifyCharacter(final Long characterId, final ModifyCharacterRequest request) {
        final Character character = characterRepository
                .findById(characterId)
                .orElseThrow(CharacterNoSuchElementException::new);

        character.update(CharacterType.valueOf(request.modelType()), request.name(), request.selectionThumbnailUrl());
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
                        character.getCharacterType().name(),
                        character.getName(),
                        character.getSelectionThumbnailUrl()))
                .toList());
    }

    public Character findById(Long characterId) {
        return characterRepository.findById(characterId)
                        .orElseThrow(CharacterNoSuchElementException::new);
    }
}
