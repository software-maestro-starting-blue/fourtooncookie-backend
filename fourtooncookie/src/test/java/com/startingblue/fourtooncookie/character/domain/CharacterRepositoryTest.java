package com.startingblue.fourtooncookie.character.domain;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.domain.ArtworkRepository;
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
    private CharacterRepository characterRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    private Artwork artwork;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        characterRepository.deleteAllInBatch();
        artworkRepository.deleteAllInBatch();

        artwork = new Artwork("Test Artwork", new URL("https://test.png"));
        artwork = artworkRepository.save(artwork);
    }

    @DisplayName("캐릭터를 저장한다.")
    @Test
    void save() throws MalformedURLException {
        // given
        String characterName = "멍멍이";
        URL characterUrl = new URL("https://멍멍이-dalle3.png");
        String basePrompt = "This is a base prompt.";
        Character character = new Character(ModelType.DALL_E_3, artwork, characterName, characterUrl, basePrompt);

        // when
        Character savedCharacter = characterRepository.save(character);

        // then
        assertThat(savedCharacter.getId()).isNotNull();
        assertThat(savedCharacter.getName()).isEqualTo(characterName);
        assertThat(savedCharacter.getModelType()).isEqualTo(ModelType.DALL_E_3);
        assertThat(savedCharacter.getSelectionThumbnailUrl()).isEqualTo(characterUrl);
        assertThat(savedCharacter.getArtwork()).isEqualTo(artwork);
        assertThat(savedCharacter.getBasePrompt()).isEqualTo(basePrompt);
    }

    @DisplayName("캐릭터를 ID로 조회한다.")
    @Test
    void findById() throws MalformedURLException {
        // given
        String basePrompt = "This is a base prompt.";
        Character character = new Character(ModelType.DALL_E_3, artwork, "멍멍이", new URL("https://멍멍이-dalle3.png"), basePrompt);
        Character savedCharacter = characterRepository.save(character);

        // when
        Optional<Character> foundCharacter = characterRepository.findById(savedCharacter.getId());

        // then
        assertThat(foundCharacter).isPresent();
        assertThat(foundCharacter.get().getName()).isEqualTo("멍멍이");
        assertThat(foundCharacter.get().getBasePrompt()).isEqualTo(basePrompt);
    }

    @DisplayName("저장된 모든 캐릭터를 가져온다.")
    @Test
    void findAll() throws MalformedURLException {
        // given
        String basePrompt1 = "This is a base prompt 1.";
        String basePrompt2 = "This is a base prompt 2.";
        Character dogDalle3Character = new Character(ModelType.DALL_E_3, artwork, "멍멍이", new URL("https://멍멍이-dalle3.png"), basePrompt1);
        Character catDalle3Character = new Character(ModelType.DALL_E_3, artwork, "나비", new URL("https://나비-dalle3.png"), basePrompt2);
        Character dogMidjourney = new Character(ModelType.MIDJOURNEY, artwork, "미드나비", new URL("https://midjourney.png"), basePrompt1);
        Character catStableDiffusionCharacter = new Character(ModelType.STABLE_DIFFUSION, artwork, "개", new URL("http://개-stable-diffusion.png"), basePrompt2);
        characterRepository.saveAll(List.of(dogDalle3Character, catDalle3Character, dogMidjourney, catStableDiffusionCharacter));

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
                .extracting(Character::getModelType)
                .containsExactlyInAnyOrder(ModelType.DALL_E_3, ModelType.DALL_E_3, ModelType.MIDJOURNEY, ModelType.STABLE_DIFFUSION);

        assertThat(savedCharacters)
                .extracting(Character::getBasePrompt)
                .containsExactlyInAnyOrder(basePrompt1, basePrompt2, basePrompt1, basePrompt2);
    }

    @DisplayName("캐릭터를 수정한다.")
    @Test
    void update() throws MalformedURLException {
        // given
        String basePrompt = "This is a base prompt.";
        Character character = new Character(ModelType.DALL_E_3, artwork, "멍멍이", new URL("https://멍멍이-dalle3.png"), basePrompt);
        Character savedCharacter = characterRepository.save(character);

        // when
        String updatedBasePrompt = "Updated base prompt.";
        savedCharacter.update(character.getModelType(), character.getArtwork(), "바뀐멍멍이", character.getSelectionThumbnailUrl(), updatedBasePrompt);
        Character updatedCharacter = characterRepository.save(savedCharacter);

        // then
        assertThat(updatedCharacter.getName()).isEqualTo("바뀐멍멍이");
        assertThat(updatedCharacter.getBasePrompt()).isEqualTo(updatedBasePrompt);
    }

    @DisplayName("캐릭터를 삭제한다.")
    @Test
    void delete() throws MalformedURLException {
        // given
        String basePrompt = "This is a base prompt.";
        Character character = new Character(ModelType.DALL_E_3, artwork, "멍멍이", new URL("https://멍멍이-dalle3.png"), basePrompt);
        Character savedCharacter = characterRepository.save(character);

        // when
        characterRepository.deleteById(savedCharacter.getId());
        Optional<Character> foundCharacter = characterRepository.findById(savedCharacter.getId());

        // then
        assertThat(foundCharacter).isNotPresent();
    }
}
