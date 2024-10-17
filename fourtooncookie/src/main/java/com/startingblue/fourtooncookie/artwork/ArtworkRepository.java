package com.startingblue.fourtooncookie.artwork;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.net.URL;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    boolean existsByTitle(String title);
    boolean existsByThumbnailUrl(URL thumbnailUrl);
}
