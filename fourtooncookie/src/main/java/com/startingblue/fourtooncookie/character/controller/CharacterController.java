package com.startingblue.fourtooncookie.character.controller;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.dto.request.AddCharacterRequest;
import com.startingblue.fourtooncookie.character.dto.request.ModifyCharacterRequest;
import com.startingblue.fourtooncookie.character.dto.response.CharacterResponse;
import com.startingblue.fourtooncookie.character.dto.response.CharacterResponses;
import com.startingblue.fourtooncookie.character.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public final class CharacterController {

    private final CharacterService characterService;

    @GetMapping("/character")
    public ResponseEntity<CharacterResponses> showCharacter() {
        final List<Character> characters = characterService.showCharacters();
        CharacterResponses characterResponses = new CharacterResponses(characters.stream()
                .map(character -> new CharacterResponse(
                        character.getId(),
                        character.getModelType().name(),
                        character.getName(),
                        character.getSelectionThumbnailUrl(),
                        character.getCalendarThumbnailUrl()))
                .toList());

        return ResponseEntity.ok(characterResponses);
    }

    @PostMapping("/character")
    public ResponseEntity<HttpStatus> addCharacter(@RequestBody final AddCharacterRequest request) {
        characterService.addCharacter(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/character/{characterId}")
    public ResponseEntity<HttpStatus> modifyCharacter(@RequestBody final ModifyCharacterRequest request, @PathVariable final Long characterId) {
        characterService.modifyCharacter(characterId, request);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/character/{characterId}")
    public ResponseEntity<HttpStatus> modifyCharacter(@PathVariable final Long characterId) {
        characterService.deleteCharacter(characterId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
