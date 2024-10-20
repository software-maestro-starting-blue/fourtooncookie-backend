package com.startingblue.fourtooncookie.artwork;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.dto.ArtworkSaveRequest;
import com.startingblue.fourtooncookie.artwork.dto.ArtworkUpdateRequest;
import com.startingblue.fourtooncookie.artwork.exception.ArtworkDuplicateException;
import com.startingblue.fourtooncookie.artwork.exception.ArtworkNotFoundException;
import com.startingblue.fourtooncookie.artwork.service.ArtworkTranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final ArtworkTranslationService artworkTranslationService;

    public void createArtwork(ArtworkSaveRequest request) {
        verifyUniqueArtwork(request.title(), request.thumbnailUrl());
        artworkRepository.save(new Artwork(request.title(), request.thumbnailUrl()));
    }

    @Transactional(readOnly = true)
    public List<Artwork> readAllArtworks() {
        return artworkRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Artwork> readAllArtworks(Locale locale) {
        return readAllArtworks().stream()
                .map(artwork -> artworkTranslationService.translateArtwork(artwork.clone(), locale))
                .toList();
    }

    public void updateArtwork(Long artworkId, ArtworkUpdateRequest request) {
        Artwork artwork = readById(artworkId);
        artwork.update(request.title(), request.thumbnailUrl());
        artworkRepository.save(artwork);
    }

    public void deleteArtwork(Long artworkId) {
        Artwork artwork = readById(artworkId);
        artworkRepository.delete(artwork);
    }

    @Transactional(readOnly = true)
    public Artwork readById(Long artworkId) {
        return artworkRepository.findById(artworkId)
                .orElseThrow(() -> new ArtworkNotFoundException("Artwork with ID " + artworkId + " not found"));
    }

    @Transactional(readOnly = true)
    public Artwork readById(Long artworkId, Locale locale) {
        return artworkTranslationService.translateArtwork(
                readById(artworkId).clone(),
                locale
        );
    }

    @Transactional(readOnly = true)
    public void verifyUniqueArtwork(String title, URL thumbnailUrl) {
        if (artworkRepository.existsByTitle(title)) {
            throw new ArtworkDuplicateException("Artwork with title '" + title + "' already exists.");
        }
        if (artworkRepository.existsByThumbnailUrl(thumbnailUrl)) {
            throw new ArtworkDuplicateException("Artwork with thumbnail URL '" + thumbnailUrl + "' already exists.");
        }
    }

}
