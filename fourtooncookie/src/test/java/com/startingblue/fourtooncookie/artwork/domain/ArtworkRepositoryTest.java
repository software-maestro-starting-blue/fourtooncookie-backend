package com.startingblue.fourtooncookie.artwork.domain;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ArtworkRepositoryTest {

    @Autowired
    private ArtworkRepository artworkRepository;

    @DisplayName("저장된 작품을 Id로 찾는다.")
    @Test
    public void findById() throws MalformedURLException {
        // Given
        Artwork artwork = new Artwork("Test Title", new URL("http://test.com/image.jpg"));
        Artwork savedArtwork = artworkRepository.save(artwork);

        // When
        Optional<Artwork> foundArtwork = artworkRepository.findById(savedArtwork.getId());

        // Then
        assertThat(foundArtwork).isPresent();
        assertThat(foundArtwork.get().getTitle()).isEqualTo("Test Title");
        assertThat(foundArtwork.get().getThumbnailUrl().toString()).isEqualTo("http://test.com/image.jpg");
    }

    @DisplayName("저장된 작품을 모두 찾는다.")
    @Test
    public void findALl() throws MalformedURLException {
        // Given
        Artwork artwork1 = new Artwork("Title 1", new URL("http://test.com/image1.jpg"));
        Artwork artwork2 = new Artwork("Title 2", new URL("http://test.com/image2.jpg"));
        artworkRepository.save(artwork1);
        artworkRepository.save(artwork2);

        // When
        List<Artwork> artworks = artworkRepository.findAll();

        // Then
        assertThat(artworks).hasSize(2);
    }

    @DisplayName("저장된 작품을 삭제한다.")
    @Test
    public void deleteById() throws MalformedURLException {
        // Given
        Artwork artwork = new Artwork("Test Title", new URL("http://test.com/image.jpg"));
        Artwork savedArtwork = artworkRepository.save(artwork);

        Long deletedId = savedArtwork.getId();

        // When
        artworkRepository.delete(savedArtwork);
        Optional<Artwork> foundArtwork = artworkRepository.findById(deletedId);

        // Then
        assertThat(foundArtwork).isNotPresent();
    }
}