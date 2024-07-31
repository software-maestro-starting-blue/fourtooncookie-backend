package com.startingblue.fourtooncookie.artwork.domain;

import com.startingblue.fourtooncookie.artwork.dto.request.ArtworkSaveRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ArtworkRepositoryTest {

    @Autowired
    private ArtworkRepository artworkRepository;

    @DisplayName("새로운 작품을 저장한다.")
    @Test
    public void save() throws MalformedURLException {
        // Given
        String newTitle = "New Artwork";
        URL newUrl = new URL("http://test.com/newimage.jpg");
        ArtworkSaveRequest request = new ArtworkSaveRequest(newTitle, newUrl);

        Artwork artwork = new Artwork(request.title(), request.thumbnailUrl());

        // When
        Artwork savedArtwork = artworkRepository.save(artwork);

        // Then
        Optional<Artwork> foundArtwork = artworkRepository.findById(savedArtwork.getId());

        assertThat(foundArtwork).isPresent();
        assertThat(foundArtwork.get().getTitle()).isEqualTo(newTitle);
        assertThat(foundArtwork.get().getThumbnailUrl()).isEqualTo(newUrl);
    }

    @DisplayName("저장된 작품을 Id로 찾는다.")
    @Test
    public void findById() throws MalformedURLException {
        // Given
        String title = "New Artwork";
        URL url = new URL("http://test.com/newimage.jpg");
        Artwork artwork = new Artwork(title, url);
        Artwork savedArtwork = artworkRepository.save(artwork);

        // When
        Optional<Artwork> foundArtwork = artworkRepository.findById(savedArtwork.getId());

        // Then
        assertThat(foundArtwork).isPresent();
        assertThat(foundArtwork.get().getTitle()).isEqualTo(title);
        assertThat(foundArtwork.get().getThumbnailUrl()).isEqualTo(url);
    }

    @DisplayName("저장된 모든 작품을 가져온다.")
    @Test
    public void findALl() throws MalformedURLException {
        // Given
        String title1 = "Title 1";
        URL url1 = new URL("http://test.com/image1.jpg");
        String title2 = "Title 2";
        URL url2 = new URL("http://test.com/image2.jpg");
        Artwork artwork1 = new Artwork(title1, url1);
        Artwork artwork2 = new Artwork(title2, url2);
        artworkRepository.saveAll(List.of(artwork1, artwork2));

        // When
        List<Artwork> savedArtworks = artworkRepository.findAll();


        // Then
        assertThat(savedArtworks).hasSize(2);
        assertThat(savedArtworks.get(0).getTitle()).isEqualTo(title1);
        assertThat(savedArtworks.get(0).getThumbnailUrl()).isEqualTo(url1);
        assertThat(savedArtworks.get(1).getTitle()).isEqualTo(title2);
        assertThat(savedArtworks.get(1).getThumbnailUrl()).isEqualTo(url2);
    }

    @DisplayName("저장된 작품을 업데이트한다.")
    @Test
    public void update() throws MalformedURLException {
        // Given
        String oldTitle = "Old Title";
        URL oldUrl = new URL("http://test.com/oldimage.jpg");
        Artwork artwork = new Artwork(oldTitle, oldUrl);
        Artwork savedArtwork = artworkRepository.save(artwork);

        String updateTitle = "updateById Title";
        URL updateUrl = new URL("http://test.com/updateimage.jpg");
        savedArtwork.update(updateTitle, updateUrl);

        // When
        artworkRepository.save(savedArtwork);

        // Then
        Optional<Artwork> updatedArtwork = artworkRepository.findById(savedArtwork.getId());
        assertThat(updatedArtwork).isPresent();
        assertThat(updatedArtwork.get().getTitle()).isEqualTo(updateTitle);
        assertThat(updatedArtwork.get().getThumbnailUrl()).isEqualTo(updateUrl);
    }


    @DisplayName("저장된 작품을 삭제한다.")
    @Test
    public void delete() throws MalformedURLException {
        // Given
        String title = "Title";
        URL url = new URL("http://test.com/newimage.jpg");
        Artwork artwork = new Artwork(title, url);
        Artwork savedArtwork = artworkRepository.save(artwork);

        Long deletedId = savedArtwork.getId();

        // When
        artworkRepository.delete(savedArtwork);
        Optional<Artwork> foundArtwork = artworkRepository.findById(deletedId);

        // Then
        assertThat(foundArtwork).isNotPresent();
    }
}