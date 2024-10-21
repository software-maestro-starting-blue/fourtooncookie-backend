package com.startingblue.fourtooncookie.character;

import com.startingblue.fourtooncookie.character.dto.CharacterSaveRequest;
import com.startingblue.fourtooncookie.character.dto.CharacterSavedResponse;
import com.startingblue.fourtooncookie.character.dto.CharacterSavedResponses;
import com.startingblue.fourtooncookie.character.dto.CharacterUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RequiredArgsConstructor
@RestController
@RequestMapping("/character")
public final class CharacterController {

    private final CharacterService characterService;

    @PostMapping
    public ResponseEntity<HttpStatus> postCharacter(@Valid @RequestBody final CharacterSaveRequest request) {
        characterService.addCharacter(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/{characterId}")
    public ResponseEntity<CharacterSavedResponse> getCharacter(@PathVariable final Long characterId) {
        CharacterSavedResponse response = CharacterSavedResponse.of(characterService.getById(characterId));
        return ResponseEntity
                .ok(response);
    }

    @GetMapping
    public ResponseEntity<CharacterSavedResponses> getAllCharacters() {
        CharacterSavedResponses responses = CharacterSavedResponses.of(characterService.getAllCharacters());
        return ResponseEntity
                .ok(responses);
    }

    @PutMapping("/{characterId}")
    public ResponseEntity<HttpStatus> putCharacter(@PathVariable final Long characterId,
                                                   @Valid @RequestBody final CharacterUpdateRequest request) {
        characterService.modifyCharacter(characterId, request);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/{characterId}")
    public ResponseEntity<HttpStatus> deleteCharacter(@PathVariable final Long characterId) {
        characterService.removeCharacter(characterId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
