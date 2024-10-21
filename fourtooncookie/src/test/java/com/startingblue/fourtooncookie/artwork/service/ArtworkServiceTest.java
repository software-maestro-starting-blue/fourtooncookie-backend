package com.startingblue.fourtooncookie.artwork.service;

import com.startingblue.fourtooncookie.artwork.ArtworkRepository;
import com.startingblue.fourtooncookie.artwork.ArtworkService;
import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.dto.ArtworkSaveRequest;
import com.startingblue.fourtooncookie.artwork.dto.ArtworkUpdateRequest;
import com.startingblue.fourtooncookie.artwork.exception.ArtworkDuplicateException;
import com.startingblue.fourtooncookie.artwork.exception.ArtworkNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArtworkServiceTest {

    @InjectMocks
    private ArtworkService artworkService;

    @Mock
    private ArtworkRepository artworkRepository;

    @Mock
    private MessageSource xmlMessageSource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("새로운 Artwork를 성공적으로 저장한다")
    void testAddArtworkSuccess() throws MalformedURLException {
        // Given
        ArtworkSaveRequest request = new ArtworkSaveRequest("New Artwork", new URL("http://example.com/new_image.jpg"));

        // 중복 확인을 위한 Mock
        when(artworkRepository.existsByTitle(anyString())).thenReturn(false);
        when(artworkRepository.existsByThumbnailUrl(any(URL.class))).thenReturn(false);

        // When
        artworkService.addArtwork(request);

        // Then
        verify(artworkRepository, times(1)).save(any(Artwork.class));
    }

    @Test
    @DisplayName("중복된 제목으로 Artwork 저장 시도 시 예외 발생")
    void testAddArtworkWithDuplicateTitle() throws MalformedURLException {
        // Given
        ArtworkSaveRequest request = new ArtworkSaveRequest("Duplicate Artwork", new URL("http://example.com/image.jpg"));

        // 중복된 제목 존재
        when(artworkRepository.existsByTitle(anyString())).thenReturn(true);
        when(artworkRepository.existsByThumbnailUrl(any())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> artworkService.addArtwork(request))
                .isInstanceOf(ArtworkDuplicateException.class)
                .hasMessage("Artwork with title, thumbnail URL already exists.");
    }

    @Test
    @DisplayName("존재하는 모든 Artwork를 성공적으로 가져온다")
    void testGetAllArtworks() throws MalformedURLException {
        // Given
        List<Artwork> artworks = Arrays.asList(
                new Artwork(1L, "Artwork 1", new URL("http://example.com/image1.jpg")),
                new Artwork(2L, "Artwork 2", new URL("http://example.com/image2.jpg"))
        );

        when(artworkRepository.findAll()).thenReturn(artworks);

        // When
        List<Artwork> result = artworkService.getAllArtworks();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Artwork 1");
        assertThat(result.get(1).getTitle()).isEqualTo("Artwork 2");
    }

    @Test
    @DisplayName("Artwork ID로 지역화된 이름을 가져온다")
    void testGetLocalizedArtworkTitle() {
        // Given
        Long artworkId = 1L;
        when(xmlMessageSource.getMessage(anyString(), any(), eq(Locale.KOREAN)))
                .thenReturn("Localized Artwork Title");

        // When
        String localizedTitle = artworkService.getLocalizedArtworkTitle(artworkId, Locale.KOREAN);

        // Then
        assertThat(localizedTitle).isEqualTo("Localized Artwork Title");
    }

    @Test
    @DisplayName("Artwork 업데이트 성공")
    void testModifyArtwork() throws MalformedURLException {
        // Given
        Artwork existingArtwork = new Artwork(1L, "Old Title", new URL("http://example.com/old_image.jpg"));
        ArtworkUpdateRequest request = new ArtworkUpdateRequest("Updated Title", new URL("http://example.com/new_image.jpg"));

        when(artworkRepository.findById(1L)).thenReturn(Optional.of(existingArtwork));

        // When
        artworkService.modifyArtwork(1L, request);

        // Then
        assertThat(existingArtwork.getTitle()).isEqualTo("Updated Title");
        assertThat(existingArtwork.getThumbnailUrl()).isEqualTo(new URL("http://example.com/new_image.jpg"));
        verify(artworkRepository, times(1)).save(existingArtwork);
    }

    @Test
    @DisplayName("존재하지 않는 Artwork 업데이트 시 ArtworkNotFoundException 발생")
    void testUpdateNonExistingArtwork() throws MalformedURLException {
        // Given
        ArtworkUpdateRequest request = new ArtworkUpdateRequest("Updated Title", new URL("http://example.com/new_image.jpg"));
        when(artworkRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> artworkService.modifyArtwork(1L, request))
                .isInstanceOf(ArtworkNotFoundException.class)
                .hasMessage("Artwork with ID 1 not found");
    }

    @Test
    @DisplayName("Artwork 삭제 성공")
    void testRemoveArtwork() throws MalformedURLException {
        // Given
        Artwork artwork = new Artwork(1L, "Artwork", new URL("http://example.com/image.jpg"));
        when(artworkRepository.findById(1L)).thenReturn(Optional.of(artwork));

        // When
        artworkService.removeArtwork(1L);

        // Then
        verify(artworkRepository, times(1)).delete(artwork);
    }

    @Test
    @DisplayName("존재하지 않는 Artwork 삭제 시 ArtworkNotFoundException 발생")
    void testDeleteNonExistingArtwork() {
        // Given
        when(artworkRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> artworkService.removeArtwork(1L))
                .isInstanceOf(ArtworkNotFoundException.class)
                .hasMessage("Artwork with ID 1 not found");
    }
}
