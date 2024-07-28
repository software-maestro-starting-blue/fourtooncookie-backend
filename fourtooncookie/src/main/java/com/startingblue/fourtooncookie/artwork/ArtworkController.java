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

@RestController
@RequiredArgsConstructor
@RequestMapping("/artwork")
public class ArtworkController {

    private final ArtworkService artworkService;

    @GetMapping
    public ResponseEntity<ArtworkSavedResponses> showArtwork() {
        ArtworkSavedResponses artworkSavedResponses = artworkService.getSavedArtworkResponses();
        return ResponseEntity
                .ok(artworkSavedResponses);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createArtwork(@Valid @RequestBody final ArtworkSaveRequest request) {
        artworkService.saveArtwork(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/{artworkId}")
    public ResponseEntity<HttpStatus> modifyArtwork(@PathVariable final Long artworkId, @Valid @RequestBody final ArtworkUpdateRequest request) {
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
