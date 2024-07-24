package com.startingblue.fourtooncookie.artwork.service;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.domain.ArtworkRepository;
import com.startingblue.fourtooncookie.artwork.dto.request.ArtworkSaveRequest;
import com.startingblue.fourtooncookie.artwork.dto.request.ArtworkUpdateRequest;
import com.startingblue.fourtooncookie.artwork.dto.response.ArtworkSavedResponses;
import com.startingblue.fourtooncookie.artwork.exception.ArtworkNoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ArtworkServiceTest {

    @Autowired
    private ArtworkService artworkService;

    @Autowired
    private ArtworkRepository artworkRepository;

    @DisplayName("저장된 모든 작품을 가져온다.")
    @Test
    public void getSavedArtworkResponses() throws MalformedURLException {
        // Given
        Artwork artwork1 = new Artwork("Title 1", new URL("http://test.com/image1.jpg"));
        Artwork artwork2 = new Artwork("Title 2", new URL("http://test.com/image2.jpg"));
        artworkRepository.save(artwork1);
        artworkRepository.save(artwork2);

        // When
        ArtworkSavedResponses responses = artworkService.getSavedArtworkResponses();

        // Then
        assertThat(responses.artworks()).hasSize(2);
    }

    @DisplayName("새로운 작품을 저장한다.")
    @Test
    public void saveArtwork() throws MalformedURLException {
        // Given
        ArtworkSaveRequest request = new ArtworkSaveRequest("New Artwork", new URL("http://test.com/newimage.jpg"));

        // When
        artworkService.saveArtwork(request);

        // Then
        Optional<Artwork> savedArtwork = artworkRepository.findAll().stream()
                .filter(artwork -> artwork.getTitle().equals("New Artwork"))
                .findFirst();

        assertThat(savedArtwork).isPresent();
        assertThat(savedArtwork.get().getTitle()).isEqualTo("New Artwork");
        assertThat(savedArtwork.get().getThumbnailUrl().toString()).isEqualTo("http://test.com/newimage.jpg");
    }

    @DisplayName("저장된 작품을 업데이트한다.")
    @Test
    public void updateArtwork() throws MalformedURLException {
        // Given
        Artwork artwork = new Artwork("Old Title", new URL("http://test.com/oldimage.jpg"));
        Artwork savedArtwork = artworkRepository.save(artwork);
        ArtworkUpdateRequest request = new ArtworkUpdateRequest("Updated Title", new URL("http://test.com/updatedimage.jpg"));

        // When
        artworkService.updateArtwork(savedArtwork.getId(), request);

        // Then
        Optional<Artwork> updatedArtwork = artworkRepository.findById(savedArtwork.getId());
        assertThat(updatedArtwork).isPresent();
        assertThat(updatedArtwork.get().getTitle()).isEqualTo("Updated Title");
        assertThat(updatedArtwork.get().getThumbnailUrl().toString()).isEqualTo("http://test.com/updatedimage.jpg");
    }

    @DisplayName("저장된 작품을 삭제한다.")
    @Test
    public void deleteArtwork() throws MalformedURLException {
        // Given
        Artwork artwork = new Artwork("Test Title", new URL("http://test.com/image.jpg"));
        Artwork savedArtwork = artworkRepository.save(artwork);

        // When
        artworkService.deleteArtwork(savedArtwork.getId());

        // Then
        Optional<Artwork> foundArtwork = artworkRepository.findById(savedArtwork.getId());
        assertThat(foundArtwork).isNotPresent();
    }

    @DisplayName("존재하지 않는 ID로 작품을 업데이트 시도시 예외를 발생시킨다.")
    @Test
    public void updateArtworkWithInvalidId() throws MalformedURLException {
        // Given
        Long notFoundArtworkId = -1L;
        ArtworkUpdateRequest request = new ArtworkUpdateRequest("Updated Title", new URL("http://test.com/updatedimage.jpg"));

        // When & Then
        assertThatThrownBy(() -> artworkService.updateArtwork(notFoundArtworkId, request))
                .isInstanceOf(ArtworkNoSuchElementException.class)
                .hasMessage("존재하지 않는 작품입니다.");
    }

    @DisplayName("존재하지 않는 ID로 작품을 삭제 시도시 예외를 발생시킨다.")
    @Test
    public void deleteArtworkWithInvalidId() {
        // Given
        Long notFoundArtworkId = -1L;

        // When & Then
        assertThatThrownBy(() -> artworkService.deleteArtwork(notFoundArtworkId))
                .isInstanceOf(ArtworkNoSuchElementException.class)
                .hasMessage("존재하지 않는 작품입니다.");
    }
}
