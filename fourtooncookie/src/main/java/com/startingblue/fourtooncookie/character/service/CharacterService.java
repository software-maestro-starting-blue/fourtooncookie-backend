package com.startingblue.fourtooncookie.character.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.ModelType;
import com.startingblue.fourtooncookie.character.dto.request.AddCharacterRequest;
import com.startingblue.fourtooncookie.character.dto.request.ModifyCharacterRequest;
import com.startingblue.fourtooncookie.character.dto.response.CharacterResponse;
import com.startingblue.fourtooncookie.character.dto.response.CharacterResponses;
import com.startingblue.fourtooncookie.character.exception.CharacterNoSuchElementException;
import com.startingblue.fourtooncookie.diary.exception.DiaryNoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class CharacterService {

    private final CharacterRepository characterRepository;

    public void addCharacter(final AddCharacterRequest request) {
        final ModelType modelType = ModelType.valueOf(request.modelType());
        final Character character = new Character(
                modelType,
                request.name(),
                request.selectionThumbnailUrl()
        );

        characterRepository.save(character);
    }

    public void modifyCharacter(final Long characterId, final ModifyCharacterRequest request) {
        final Character character = characterRepository
                .findById(characterId)
                .orElseThrow(CharacterNoSuchElementException::new);

        character.changeModelType(ModelType.from(request.modelType()));
        character.changeName(request.name());
        character.changeSelectionThumbnailUrl(request.selectionThumbnailUrl());
        characterRepository.save(character);
    }

    public void deleteCharacter(final Long characterId) {
        characterRepository.deleteById(characterId);
    }

    @Transactional(readOnly = true)
    public CharacterResponses showCharactersByModelType(String modelType) {
        ModelType matchModelType = ModelType.from(modelType);
        final List<Character> characters = characterRepository.findAllByModelType(matchModelType);

        return new CharacterResponses(characters.stream()
                .filter(character -> character.getModelType().equals(matchModelType))
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
