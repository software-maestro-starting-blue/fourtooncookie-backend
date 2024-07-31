package com.startingblue.fourtooncookie.artwork.service;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.domain.ArtworkRepository;
import com.startingblue.fourtooncookie.artwork.dto.request.ArtworkSaveRequest;
import com.startingblue.fourtooncookie.artwork.dto.request.ArtworkUpdateRequest;
import com.startingblue.fourtooncookie.artwork.exception.ArtworkDuplicateException;
import com.startingblue.fourtooncookie.artwork.exception.ArtworkNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtworkService {

    private final ArtworkRepository artworkRepository;

    public void createArtwork(ArtworkSaveRequest request) {
        validateUniqueArtwork(request.title(), request.thumbnailUrl());
        artworkRepository.save(new Artwork(request.title(), request.thumbnailUrl()));
    }

    @Transactional(readOnly = true)
    public List<Artwork> readAllArtworks() {
        return artworkRepository.findAll();
    }

    public void updateArtwork(Long artworkId, ArtworkUpdateRequest request) {
        Artwork artwork = findById(artworkId);
        artwork.update(request.title(), request.thumbnailUrl());
        artworkRepository.save(artwork);
    }

    public void deleteArtwork(Long artworkId) {
        Artwork artwork = findById(artworkId);
        artworkRepository.delete(artwork);
    }

    @Transactional(readOnly = true)
    public Artwork findById(Long artworkId) {
        return artworkRepository.findById(artworkId)
                .orElseThrow(() -> new ArtworkNotFoundException("Artwork with ID " + artworkId + " not found"));
    }

    @Transactional(readOnly = true)
    public void validateUniqueArtwork(String title, URL thumbnailUrl) {
        if (artworkRepository.existsByTitle(title)) {
            throw new ArtworkDuplicateException("Artwork with title '" + title + "' already exists.");
        }
        if (artworkRepository.existsByThumbnailUrl(thumbnailUrl)) {
            throw new ArtworkDuplicateException("Artwork with thumbnail URL '" + thumbnailUrl + "' already exists.");
        }
    }
}


