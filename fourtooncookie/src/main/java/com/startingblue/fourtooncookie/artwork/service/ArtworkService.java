package com.startingblue.fourtooncookie.artwork.service;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.domain.ArtworkRepository;
import com.startingblue.fourtooncookie.artwork.dto.request.ArtworkSaveRequest;
import com.startingblue.fourtooncookie.artwork.dto.request.ArtworkUpdateRequest;
import com.startingblue.fourtooncookie.artwork.dto.response.ArtworkSavedResponse;
import com.startingblue.fourtooncookie.artwork.dto.response.ArtworkSavedResponses;
import com.startingblue.fourtooncookie.artwork.exception.ArtworkNoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtworkService {

    private final ArtworkRepository artworkRepository;

    public ArtworkSavedResponses getSavedArtworkResponses() {
        List<Artwork> artworks = artworkRepository.findAll();
        List<ArtworkSavedResponse> artworkSavedResponses = artworks.stream()
                .map(ArtworkSavedResponse::of)
                .toList();
        return new ArtworkSavedResponses(artworkSavedResponses);
    }

    public void saveArtwork(ArtworkSaveRequest request) {
        Artwork artwork = new Artwork(request.title(), request.thumnailUrl());
        artworkRepository.save(artwork);
    }

    public void updateArtwork(Long artworkId, ArtworkUpdateRequest request) {
        Artwork artwork = findById(artworkId);
        artwork.update(request.title(), request.thumnailUrl());
        artworkRepository.save(artwork);
    }

    public void deleteArtwork(Long artworkId) {
        Artwork artwork = findById(artworkId);
        artworkRepository.delete(artwork);
    }

    public Artwork findById(Long artworkId) {
        return artworkRepository.findById(artworkId)
                .orElseThrow(ArtworkNoSuchElementException::new);
    }
}
