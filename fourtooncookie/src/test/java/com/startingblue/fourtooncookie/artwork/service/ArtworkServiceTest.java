package com.startingblue.fourtooncookie.artwork.service;

import com.startingblue.fourtooncookie.artwork.ArtworkService;
import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.domain.ArtworkRepository;
import com.startingblue.fourtooncookie.artwork.dto.request.ArtworkSaveRequest;
import com.startingblue.fourtooncookie.artwork.dto.request.ArtworkUpdateRequest;
import com.startingblue.fourtooncookie.artwork.dto.response.ArtworkSavedResponses;
import com.startingblue.fourtooncookie.artwork.exception.ArtworkNotFoundException;
import com.startingblue.fourtooncookie.artwork.exception.ArtworkDuplicateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ArtworkServiceTest {

    @Autowired
    private ArtworkService artworkService;

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    MessageSource messageSource;

    @DisplayName("저장된 모든 작품을 가져온다.")
    @Test
    public void readAllArtworks() throws MalformedURLException {
        // Given
        String title1 = "랜덤";
        URL url1 = new URL("http://test.com/image1.jpg");
        String title2 = "말랑";
        URL url2 = new URL("http://test.com/image2.jpg");
        Artwork artwork1 = new Artwork(title1, url1);
        Artwork artwork2 = new Artwork(title2, url2);
        artworkRepository.save(artwork1);
        artworkRepository.save(artwork2);

        // When
        ArtworkSavedResponses responses = ArtworkSavedResponses.of(artworkService.readAllArtworks(Locale.KOREA));

        // Then
        assertThat(responses.artworks()).hasSize(2);
        assertThat(responses.artworks().get(0).title()).isEqualTo(title1);
        assertThat(responses.artworks().get(0).thumbnailUrl()).isEqualTo(url1);
        assertThat(responses.artworks().get(1).title()).isEqualTo(title2);
        assertThat(responses.artworks().get(1).thumbnailUrl()).isEqualTo(url2);
    }

    @DisplayName("새로운 작품을 저장한다.")
    @Test
    public void createArtwork() throws MalformedURLException {
        // Given
        String newTitle = "New Artwork";
        URL newUrl = new URL("http://test.com/newimage.jpg");
        ArtworkSaveRequest request = new ArtworkSaveRequest(newTitle, newUrl);

        // When
        artworkService.createArtwork(request);

        // Then
        Optional<Artwork> savedArtwork = artworkRepository.findAll().stream()
                .filter(artwork -> artwork.getTitle().equals(newTitle))
                .findFirst();

        assertThat(savedArtwork).isPresent();
        assertThat(savedArtwork.get().getTitle()).isEqualTo(newTitle);
        assertThat(savedArtwork.get().getThumbnailUrl()).isEqualTo(newUrl);
    }

    @DisplayName("저장된 작품을 업데이트한다.")
    @Test
    public void updateArtwork() throws MalformedURLException {
        // Given
        String oldTitle = "Old Title";
        URL oldUrl = new URL("http://test.com/oldimage.jpg");
        Artwork artwork = new Artwork(oldTitle, oldUrl);
        Artwork savedArtwork = artworkRepository.save(artwork);

        String updateTitle = "updateById Title";
        URL updateUrl = new URL("http://test.com/updateimage.jpg");
        ArtworkUpdateRequest request = new ArtworkUpdateRequest(updateTitle, updateUrl);

        // When
        artworkService.updateArtwork(savedArtwork.getId(), request);

        // Then
        Optional<Artwork> updatedArtwork = artworkRepository.findById(savedArtwork.getId());
        assertThat(updatedArtwork).isPresent();
        assertThat(updatedArtwork.get().getTitle()).isEqualTo(updateTitle);
        assertThat(updatedArtwork.get().getThumbnailUrl()).isEqualTo(updateUrl);
    }

    @DisplayName("저장된 작품을 삭제한다.")
    @Test
    public void deleteArtwork() throws MalformedURLException {
        // Given
        String title = "Title";
        URL url = new URL("http://test.com/image.jpg");

        Artwork artwork = new Artwork(title, url);
        Artwork savedArtwork = artworkRepository.save(artwork);

        Long deleteId = savedArtwork.getId();

        // When
        artworkService.deleteArtwork(deleteId);

        // Then
        Optional<Artwork> foundArtwork = artworkRepository.findById(deleteId);
        assertThat(foundArtwork).isNotPresent();
    }

    @DisplayName("존재하지 않는 ID로 작품을 업데이트 시도시 예외를 발생시킨다.")
    @Test
    public void updateArtworkWithInvalidId() throws MalformedURLException {
        // Given
        Long notFoundArtworkId = -1L;
        String updateTitle = "Update Title";
        URL updateUrl = new URL("http://test.com/updateimage.jpg");
        ArtworkUpdateRequest request = new ArtworkUpdateRequest(updateTitle, updateUrl);

        // When & Then
        assertThatThrownBy(() -> artworkService.updateArtwork(notFoundArtworkId, request))
                .isInstanceOf(ArtworkNotFoundException.class)
                .hasMessage("Artwork with ID -1 not found");
    }

    @DisplayName("존재하지 않는 ID로 작품을 삭제 시도시 예외를 발생시킨다.")
    @Test
    public void deleteArtworkWithInvalidId() {
        // Given
        Long notFoundArtworkId = -1L;

        // When & Then
        assertThatThrownBy(() -> artworkService.deleteArtwork(notFoundArtworkId))
                .isInstanceOf(ArtworkNotFoundException.class)
                .hasMessage("Artwork with ID -1 not found");
    }

    @DisplayName("중복된 제목으로 작품 저장 시도시 예외를 발생시킨다.")
    @Test
    public void createArtworkWithDuplicateTitle() throws MalformedURLException {
        // Given
        String title = "Duplicate Title";
        URL url = new URL("http://test.com/image.jpg");
        Artwork artwork = new Artwork(title, url);
        artworkRepository.save(artwork);

        ArtworkSaveRequest request = new ArtworkSaveRequest(title, new URL("http://test.com/newimage.jpg"));

        // When & Then
        assertThatThrownBy(() -> artworkService.createArtwork(request))
                .isInstanceOf(ArtworkDuplicateException.class)
                .hasMessage("Artwork with title 'Duplicate Title' already exists.");
    }

    @DisplayName("중복된 URL로 작품 저장 시도시 예외를 발생시킨다.")
    @Test
    public void createArtworkWithDuplicateUrl() throws MalformedURLException {
        // Given
        String title = "New Title";
        URL url = new URL("http://test.com/duplicateimage.jpg");
        Artwork artwork = new Artwork(title, url);
        artworkRepository.save(artwork);

        ArtworkSaveRequest request = new ArtworkSaveRequest("Another Title", url);

        // When & Then
        assertThatThrownBy(() -> artworkService.createArtwork(request))
                .isInstanceOf(ArtworkDuplicateException.class)
                .hasMessage("Artwork with thumbnail URL 'http://test.com/duplicateimage.jpg' already exists.");
    }
}
