package com.startingblue.fourtooncookie.artwork;

import com.startingblue.fourtooncookie.artwork.dto.request.ArtworkSaveRequest;
import com.startingblue.fourtooncookie.artwork.dto.request.ArtworkUpdateRequest;
import com.startingblue.fourtooncookie.artwork.dto.response.ArtworkSavedResponses;
import com.startingblue.fourtooncookie.artwork.service.ArtworkService;
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

    @GetMapping
    public ResponseEntity<ArtworkSavedResponses> readAllArtwork(Locale locale) {
        return ResponseEntity
                .ok(ArtworkSavedResponses.of(artworkService.readAllArtworks(locale)));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createArtwork(@Valid @RequestBody final ArtworkSaveRequest request) {
        artworkService.createArtwork(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/{artworkId}")
    public ResponseEntity<HttpStatus> updateArtwork(@PathVariable final Long artworkId, @Valid @RequestBody final ArtworkUpdateRequest request) {
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
