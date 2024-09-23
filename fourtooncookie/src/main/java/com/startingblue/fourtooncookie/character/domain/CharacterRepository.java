package com.startingblue.fourtooncookie.character.domain;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.global.domain.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {
    boolean existsByName(String name);
    boolean existsByArtwork(Artwork artwork);
    boolean existsByPaymentType(PaymentType paymentType);
    boolean existsByCharacterVisionType(CharacterVisionType characterVisionType);
}
