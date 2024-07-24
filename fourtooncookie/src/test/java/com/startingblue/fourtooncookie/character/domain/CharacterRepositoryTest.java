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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CharacterRepositoryTest {

    @Autowired
    CharacterRepository characterRepository;

    @BeforeEach
    public void setUp() {
        characterRepository.deleteAllInBatch();
    }

    @DisplayName("캐릭터를 저장한다.")
    @Test
    void save() throws MalformedURLException {
        // given
        Character character = new Character(CharacterVisionType.DALL_E_3, "멍멍이", new URL("https://멍멍이-dalle3.png"));

        // when
        Character savedCharacter = characterRepository.save(character);

        // then
        assertThat(savedCharacter.getId()).isNotNull();
        assertThat(savedCharacter.getName()).isEqualTo("멍멍이");
        assertThat(savedCharacter.getCharacterVisionType()).isEqualTo(CharacterVisionType.DALL_E_3);
        assertThat(savedCharacter.getSelectionThumbnailUrl()).isEqualTo(new URL("https://멍멍이-dalle3.png"));
    }

    @DisplayName("캐릭터를 ID로 조회한다.")
    @Test
    void findById() throws MalformedURLException {
        // given
        Character character = new Character(CharacterVisionType.DALL_E_3, "멍멍이", new URL("https://멍멍이-dalle3.png"));
        Character savedCharacter = characterRepository.save(character);

        // when
        Optional<Character> foundCharacter = characterRepository.findById(savedCharacter.getId());

        // then
        assertThat(foundCharacter).isPresent();
        assertThat(foundCharacter.get().getName()).isEqualTo("멍멍이");
    }

    @DisplayName("저장된 모든 캐릭터를 가져온다.")
    @Test
    void findAll() throws MalformedURLException {
        // given
        Character dogDalle3Character = new Character(CharacterVisionType.DALL_E_3, "멍멍이", new URL("https://멍멍이-dalle3.png"));
        Character catDalle3Character = new Character(CharacterVisionType.DALL_E_3, "나비", new URL("https://나비-dalle3.png"));
        Character dogMidjourney = new Character(CharacterVisionType.MIDJOURNEY, "미드나비", new URL("https://midjourney.png"));
        Character catStableDiffusionCharacter = new Character(CharacterVisionType.STABLE_DIFFUSION, "개", new URL("http://개-stable-diffusion.png"));
        characterRepository.saveAll(List.of(dogDalle3Character, catDalle3Character, dogMidjourney ,catStableDiffusionCharacter));

        // when
        List<Character> savedCharacters = characterRepository.findAll();

        // then
        assertThat(savedCharacters).hasSize(4);
        assertThat(savedCharacters)
                .extracting(Character::getName)
                .containsExactlyInAnyOrder("멍멍이", "나비", "미드나비", "개");

        assertThat(savedCharacters)
                .extracting(Character::getSelectionThumbnailUrl)
                .containsExactlyInAnyOrder(
                        new URL("https://멍멍이-dalle3.png"),
                        new URL("https://나비-dalle3.png"),
                        new URL("https://midjourney.png"),
                        new URL("http://개-stable-diffusion.png")
                );

        assertThat(savedCharacters)
                .extracting(Character::getCharacterVisionType)
                .containsExactlyInAnyOrder(CharacterVisionType.DALL_E_3, CharacterVisionType.DALL_E_3, CharacterVisionType.MIDJOURNEY, CharacterVisionType.STABLE_DIFFUSION);
    }

    @DisplayName("캐릭터를 수정한다.")
    @Test
    void update() throws MalformedURLException {
        // given
        Character character = new Character(CharacterVisionType.DALL_E_3, "멍멍이", new URL("https://멍멍이-dalle3.png"));
        Character savedCharacter = characterRepository.save(character);

        CharacterVisionType newCharacterVisionType = CharacterVisionType.STABLE_DIFFUSION;
        String newName = "바뀐멍멍이";
        URL newURL = new URL("https://바뀐멍멍이-stable-diffusion.png");

        // when
        savedCharacter.update(newCharacterVisionType, newName, newURL);
        Character updatedCharacter = characterRepository.save(savedCharacter);

        // then
        assertThat(updatedCharacter.getName()).isEqualTo("바뀐멍멍이");
    }


    @DisplayName("캐릭터를 삭제한다.")
    @Test
    void delete() throws MalformedURLException {
        // given
        Character character = new Character(CharacterVisionType.DALL_E_3, "멍멍이", new URL("https://멍멍이-dalle3.png"));
        Character savedCharacter = characterRepository.save(character);

        // when
        characterRepository.deleteById(savedCharacter.getId());
        Optional<Character> foundCharacter = characterRepository.findById(savedCharacter.getId());

        // then
        assertThat(foundCharacter).isNotPresent();
    }
}
