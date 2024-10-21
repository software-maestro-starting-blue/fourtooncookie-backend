package com.startingblue.fourtooncookie.artwork;

import com.startingblue.fourtooncookie.artwork.dto.ArtworkSaveRequest;
import com.startingblue.fourtooncookie.artwork.dto.ArtworkSavedResponses;
import com.startingblue.fourtooncookie.artwork.dto.ArtworkUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/artwork")
public class ArtworkController {

    private final ArtworkService artworkService;

    @PostMapping
    public ResponseEntity<HttpStatus> postArtwork(@Valid @RequestBody final ArtworkSaveRequest request) {
        artworkService.createArtwork(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping
    public ResponseEntity<ArtworkSavedResponses> getAllArtwork(Locale locale) {
        return ResponseEntity
                .ok(ArtworkSavedResponses.of(artworkService.readAllArtworks(locale)));
    }

    @PutMapping("/{artworkId}")
    public ResponseEntity<HttpStatus> putArtwork(@PathVariable final Long artworkId, @Valid @RequestBody final ArtworkUpdateRequest request) {
        artworkService.updateArtwork(artworkId, request);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/{artworkId}")
    public ResponseEntity<HttpStatus> deleteArtwork(@PathVariable final Long artworkId) {
        artworkService.deleteArtwork(artworkId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
