package com.startingblue.fourtooncookie.artwork.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.net.URL;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    boolean existsByTitle(String title);

    boolean existsByThumbnailUrl(URL thumbnailUrl);
}
