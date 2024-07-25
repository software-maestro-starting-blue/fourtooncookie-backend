package com.startingblue.fourtooncookie.character.domain;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.domain.ArtworkRepository;
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
class CharacterRepositoryTest {

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private ArtworkRepository artworkRepository;


    @DisplayName("캐릭터를 저장한다.")
    @Test
    void save() throws MalformedURLException {
        // given
        String characterName = "멍멍이";
        Artwork artwork = new Artwork("Test Artwork", new URL("https://test.png"));
        URL characterUrl = new URL("https://멍멍이-dalle3.png");
        String basePrompt = "This is a base prompt.";
        Character character = new Character(CharacterVisionType.DALL_E_3, artwork, characterName, characterUrl, basePrompt);

        // when
        Character savedCharacter = characterRepository.save(character);

        // then
        assertThat(savedCharacter.getId()).isNotNull();
        assertThat(savedCharacter.getName()).isEqualTo(characterName);
        assertThat(savedCharacter.getCharacterVisionType()).isEqualTo(CharacterVisionType.DALL_E_3);
        assertThat(savedCharacter.getSelectionThumbnailUrl()).isEqualTo(characterUrl);
        assertThat(savedCharacter.getArtwork()).isEqualTo(artwork);
        assertThat(savedCharacter.getBasePrompt()).isEqualTo(basePrompt);
    }

    @DisplayName("캐릭터를 ID로 조회한다.")
    @Test
    void findById() throws MalformedURLException {
        // given
        Artwork artwork = new Artwork("Test Artwork", new URL("https://test.png"));
        String characterName = "멍멍이";
        URL characterUrl = new URL("https://test.png");
        String basePrompt = "This is a base prompt.";
        Character character = new Character(CharacterVisionType.DALL_E_3, artwork, characterName, characterUrl, basePrompt);
        Character savedCharacter = characterRepository.save(character);

        // when
        Optional<Character> foundCharacter = characterRepository.findById(savedCharacter.getId());

        // then
        assertThat(foundCharacter).isPresent();
        assertThat(foundCharacter.get().getCharacterVisionType()).isEqualTo(CharacterVisionType.DALL_E_3);
        assertThat(foundCharacter.get().getName()).isEqualTo(characterName);
        assertThat(foundCharacter.get().getSelectionThumbnailUrl()).isEqualTo(characterUrl);
        assertThat(foundCharacter.get().getBasePrompt()).isEqualTo(basePrompt);
    }

    @DisplayName("저장된 모든 캐릭터를 조회한다.")
    @Test
    void findAll() throws MalformedURLException {
        // given
        Artwork artwork = new Artwork("Test Artwork", new URL("https://test.png"));
        artworkRepository.save(artwork);

        String dogDalle3CharacterName = "멍멍이";
        URL dogDalle3Url = new URL("https://멍멍이-dalle3.png");
        String dogDalle3CharacterBasePrompt = "This is a base prompt dog.";
        Character dogDalle3Character = new Character(CharacterVisionType.DALL_E_3, artwork, dogDalle3CharacterName, dogDalle3Url, dogDalle3CharacterBasePrompt);

        String catDalle3CharacterName = "나비";
        URL catDalle3Url = new URL("https://나비-dalle3.png");
        String catDalle3CharacterBasePrompt = "This is a base prompt cat.";
        Character catDalle3Character = new Character(CharacterVisionType.DALL_E_3, artwork, catDalle3CharacterName, catDalle3Url, catDalle3CharacterBasePrompt);

        String midjourneyCharacterName = "미드나비";
        URL midjourneyCharacterUrl = new URL("https://midjourney.png");
        String midjourneyCharacterNameCharacterBasePrompt = "This is a base prompt mid cat.";
        Character dogMidjourney = new Character(CharacterVisionType.MIDJOURNEY, artwork, midjourneyCharacterName, midjourneyCharacterUrl, midjourneyCharacterNameCharacterBasePrompt);

        String stableDiffusionCharacterName = "스테이블디퓨전";
        URL stableDiffusionCharacterUrl = new URL("https://stable-diffusion.png");
        String stableDiffusionCharacterNameCharacterBasePrompt = "This is a base prompt stable diffusion.";
        Character catStableDiffusionCharacter = new Character(CharacterVisionType.STABLE_DIFFUSION, artwork, stableDiffusionCharacterName, stableDiffusionCharacterUrl, stableDiffusionCharacterNameCharacterBasePrompt);
        characterRepository.saveAll(List.of(dogDalle3Character, catDalle3Character, dogMidjourney, catStableDiffusionCharacter));

        // when
        List<Character> savedCharacters = characterRepository.findAll();

        // then
        assertThat(savedCharacters).hasSize(4);
        assertThat(savedCharacters)
                .extracting(Character::getCharacterVisionType)
                .containsExactly(CharacterVisionType.DALL_E_3, CharacterVisionType.DALL_E_3, CharacterVisionType.MIDJOURNEY, CharacterVisionType.STABLE_DIFFUSION);
        assertThat(savedCharacters)
                .extracting(Character::getName)
                .containsExactly(
                        dogDalle3CharacterName,
                        catDalle3CharacterName,
                        midjourneyCharacterName,
                        stableDiffusionCharacterName);
        assertThat(savedCharacters)
                .extracting(Character::getSelectionThumbnailUrl)
                .containsExactly(
                        dogDalle3Url,
                        catDalle3Url,
                        midjourneyCharacterUrl,
                        stableDiffusionCharacterUrl
                );
        assertThat(savedCharacters)
                .extracting(Character::getBasePrompt)
                .containsExactly(
                        dogDalle3CharacterBasePrompt,
                        catDalle3CharacterBasePrompt,
                        midjourneyCharacterNameCharacterBasePrompt,
                        stableDiffusionCharacterNameCharacterBasePrompt
                );
        Character savedStableDiffusionCharacter = savedCharacters.get(3);
        assertThat(savedStableDiffusionCharacter.getCharacterVisionType()).isEqualTo(CharacterVisionType.STABLE_DIFFUSION);
        assertThat(savedStableDiffusionCharacter.getName()).isEqualTo(stableDiffusionCharacterName);
        assertThat(savedStableDiffusionCharacter.getArtwork()).isEqualTo(savedStableDiffusionCharacter.getArtwork());
        assertThat(savedStableDiffusionCharacter.getSelectionThumbnailUrl()).isEqualTo(stableDiffusionCharacterUrl);
        assertThat(savedStableDiffusionCharacter.getBasePrompt()).isEqualTo(stableDiffusionCharacterNameCharacterBasePrompt);
    }

    @DisplayName("특정 캐릭터를 수정한다.")
    @Test
    void update() throws MalformedURLException {
        // given
        Character character = new Character(CharacterVisionType.DALL_E_3, new Artwork("Test Artwork", new URL("https://test.png")), "멍멍이", new URL("https://멍멍이-dalle3.png"), "This is a base prompt");
        Character savedCharacter = characterRepository.save(character);

        CharacterVisionType updateCharacterVisionType = CharacterVisionType.STABLE_DIFFUSION;
        Artwork updateArtwork = new Artwork("update Artwork", new URL("https://updateArtwork.png"));
        String updateCharacterName = "바뀐멍멍이";
        URL updateUrl = new URL("https://test.png");
        String updateBasePrompt = "Updated base prompt.";

        // when
        savedCharacter.update(updateCharacterVisionType, updateArtwork, updateCharacterName, updateUrl, updateBasePrompt);
        Character updatedCharacter = characterRepository.save(savedCharacter);

        // then
        assertThat(updatedCharacter.getCharacterVisionType()).isEqualTo(updateCharacterVisionType);
        assertThat(updatedCharacter.getArtwork()).isEqualTo(updateArtwork);
        assertThat(updatedCharacter.getName()).isEqualTo(updateCharacterName);
        assertThat(updatedCharacter.getSelectionThumbnailUrl()).isEqualTo(updateUrl);
        assertThat(updatedCharacter.getBasePrompt()).isEqualTo(updateBasePrompt);
    }

    @DisplayName("특정 캐릭터를 삭제한다.")
    @Test
    void delete() throws MalformedURLException {
        // given
        Artwork artwork = new Artwork("Test Artwork", new URL("https://test.png"));
        String basePrompt = "This is a base prompt.";
        Character character = new Character(CharacterVisionType.DALL_E_3, artwork, "멍멍이", new URL("https://멍멍이-dalle3.png"), basePrompt);
        Character savedCharacter = characterRepository.save(character);

        // when
        characterRepository.deleteById(savedCharacter.getId());
        Optional<Character> foundCharacter = characterRepository.findById(savedCharacter.getId());

        // then
        assertThat(foundCharacter).isNotPresent();
    }
}
