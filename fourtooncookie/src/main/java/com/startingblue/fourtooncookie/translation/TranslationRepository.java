package com.startingblue.fourtooncookie.translation;

import com.startingblue.fourtooncookie.translation.domain.Translation;
import com.startingblue.fourtooncookie.translation.domain.TranslationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranslationRepository extends JpaRepository<Translation, TranslationId> {
    boolean existsById(TranslationId translationId);
}
