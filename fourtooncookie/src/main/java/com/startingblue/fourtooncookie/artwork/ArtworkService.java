package com.startingblue.fourtooncookie.artwork;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.dto.ArtworkSaveRequest;
import com.startingblue.fourtooncookie.artwork.dto.ArtworkUpdateRequest;
import com.startingblue.fourtooncookie.artwork.exception.ArtworkDuplicateException;
import com.startingblue.fourtooncookie.artwork.exception.ArtworkNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final MessageSource xmlMessageSource;

    public void addArtwork(ArtworkSaveRequest request) {
        validateUniqueArtwork(request.title(), request.thumbnailUrl());
        artworkRepository.save(new Artwork(request.title(), request.thumbnailUrl()));
    }

    @Transactional(readOnly = true)
    public Artwork getById(Long artworkId) {
        return artworkRepository.findById(artworkId)
                .orElseThrow(() -> new ArtworkNotFoundException("Artwork with ID " + artworkId + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Artwork> getAllArtworks() {
        return artworkRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Artwork> getAllArtworks(Locale locale) {
        return getAllArtworks().stream()
                .map(artwork -> getArtworkWithNameChange(artwork, locale))
                .toList();
    }

    public Artwork getArtworkWithNameChange(Artwork artwork, Locale locale) {
        return new Artwork(artwork.getId(), getLocalizedArtworkTitle(artwork.getId(), locale), artwork.getThumbnailUrl());
    }

    public String getLocalizedArtworkTitle(Long artworkId, Locale locale) {
        return Objects.requireNonNull(xmlMessageSource.getMessage("artwork.name." + artworkId, null, locale));
    }

    public void modifyArtwork(Long artworkId, ArtworkUpdateRequest request) {
        Artwork artwork = getById(artworkId);
        artwork.update(request.title(), request.thumbnailUrl());
        artworkRepository.save(artwork);
    }

    public void removeArtwork(Long artworkId) {
        Artwork artwork = getById(artworkId);
        artworkRepository.delete(artwork);
    }

    @Transactional(readOnly = true)
    public void validateUniqueArtwork(String title, URL thumbnailUrl) {
        if (artworkRepository.existsByTitle(title) && artworkRepository.existsByThumbnailUrl(thumbnailUrl)) {
            throw new ArtworkDuplicateException("Artwork with title, thumbnail URL already exists.");
        }
    }

}
