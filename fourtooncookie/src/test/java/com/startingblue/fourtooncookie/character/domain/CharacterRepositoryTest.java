package com.startingblue.fourtooncookie.character.domain;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CharacterRepositoryTest {

    @Autowired
    CharacterRepository characterRepository;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        characterRepository.deleteAllInBatch();
    }

    @DisplayName("모든 캐릭터를 저장한다.")
    @Test
    void save() throws MalformedURLException {
        // given
        Character dogDalle3Character = new Character(ModelType.DALL_E_3, "멍멍이", new URL("https://멍멍이-dalle3.png"));
        Character catDalle3Character = new Character(ModelType.DALL_E_3, "나비", new URL("https://나비-dalle3.png"));
        Character catStableDiffusionCharacter = new Character(ModelType.STABLE_DIFFUSION, "개", new URL("http://개-stable-diffusion.png"));
        characterRepository.saveAll(List.of(dogDalle3Character, catDalle3Character, catStableDiffusionCharacter));

        // when
        List<Character> savedCharacters = characterRepository.findAll();

        // then
        assertThat(savedCharacters).hasSize(3);
        assertThat(savedCharacters)
                .extracting(Character::getName)
                .containsExactlyInAnyOrder("멍멍이", "나비", "개");

        assertThat(savedCharacters)
                .extracting(Character::getSelectionThumbnailUrl)
                .containsExactlyInAnyOrder(
                        new URL("https://멍멍이-dalle3.png"),
                        new URL("https://나비-dalle3.png"),
                        new URL("http://개-stable-diffusion.png")
                );
    }
}
