package com.startingblue.fourtooncookie.character;

import com.startingblue.fourtooncookie.character.dto.request.AddCharacterRequest;
import com.startingblue.fourtooncookie.character.dto.request.ModifyCharacterRequest;
import com.startingblue.fourtooncookie.character.dto.response.CharacterResponses;
import com.startingblue.fourtooncookie.character.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public final class CharacterController {

    private final CharacterService characterService;

    @GetMapping("/character")
    public ResponseEntity<CharacterResponses> showCharactersByModelType(@RequestParam(defaultValue = "dall_e_3") String modelType) {
        final CharacterResponses characterResponses = characterService.showCharactersByModelType(modelType);

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
    public ResponseEntity<HttpStatus> deleteCharacter(@PathVariable final Long characterId) {
        characterService.deleteCharacter(characterId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
