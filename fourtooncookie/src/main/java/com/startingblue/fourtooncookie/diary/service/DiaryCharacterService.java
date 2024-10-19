package com.startingblue.fourtooncookie.diary.service;

import com.startingblue.fourtooncookie.character.CharacterService;
import com.startingblue.fourtooncookie.character.domain.Character;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiaryCharacterService {

    private final CharacterService characterService;

    public Character readById(Long characterId) {
        return characterService.readById(characterId);
    }

}
