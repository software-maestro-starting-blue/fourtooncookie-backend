package com.startingblue.fourtooncookie.character.service;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.service.ArtworkService;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.character.dto.request.AddCharacterRequest;
import com.startingblue.fourtooncookie.character.dto.request.ModifyCharacterRequest;
import com.startingblue.fourtooncookie.character.dto.response.CharacterResponse;
import com.startingblue.fourtooncookie.character.dto.response.CharacterResponses;
import com.startingblue.fourtooncookie.character.exception.CharacterNoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CharacterServiceTest {

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private ArtworkService artworkService;

    @InjectMocks
    private CharacterService characterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("캐릭터를 추가한다.")
    @Test
    void addCharacter() throws MalformedURLException {
        // given
        AddCharacterRequest request = new AddCharacterRequest("DALL_E_3", 1L, "멍멍이", new URL("https://멍멍이-dalle3.png"), "This is a base prompt");

        CharacterVisionType characterVisionType = CharacterVisionType.valueOf(request.characterVisionType());
        Artwork artwork = new Artwork("Test Artwork", new URL("https://test.png"));
        Character character = new Character(characterVisionType, artwork, request.name(), request.selectionThumbnailUrl(), request.basePrompt());

        // when
        when(artworkService.findById(request.artworkId())).thenReturn(artwork);
        when(characterRepository.save(any(Character.class))).thenReturn(character);
        characterService.addCharacter(request);

        // then
        assertThat(character.getCharacterVisionType()).isEqualTo(characterVisionType);
        assertThat(character.getArtwork()).isEqualTo(artwork);
        assertThat(character.getName()).isEqualTo(request.name());
        assertThat(character.getSelectionThumbnailUrl()).isEqualTo(request.selectionThumbnailUrl());
        assertThat(character.getBasePrompt()).isEqualTo(request.basePrompt());
        verify(characterRepository, times(1)).save(any(Character.class));
    }

    @DisplayName("ID로 캐릭터를 조회한다.")
    @Test
    void findCharacterById() throws MalformedURLException {
        // given
        Long characterId = 1L;
        String basePrompt = "This is a base prompt";
        Character character = new Character(CharacterVisionType.DALL_E_3, new Artwork("Test Artwork", new URL("https://test.png")), "멍멍이", new URL("https://멍멍이-dalle3.png"), basePrompt);
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));

        // when
        Character foundCharacter = characterService.findById(characterId);

        // then
        assertThat(foundCharacter).isNotNull();
        assertThat(foundCharacter.getName()).isEqualTo("멍멍이");
        assertThat(foundCharacter.getBasePrompt()).isEqualTo(basePrompt);
        verify(characterRepository, times(1)).findById(characterId);
    }

    @DisplayName("존재하지 않는 캐릭터 조회시 예외를 발생시킨다.")
    @Test
    void throwExceptionWhenCharacterNotFoundById() {
        // given
        Long notFoundCharacterId = -1L;
        when(characterRepository.findById(notFoundCharacterId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CharacterNoSuchElementException.class, () -> characterService.findById(notFoundCharacterId));
        verify(characterRepository, times(1)).findById(notFoundCharacterId);
    }

    @DisplayName("저장된 모든 캐릭터를 조회한다.")
    @Test
    void showAllCharacters() throws MalformedURLException {
        // given
        CharacterVisionType character1VisionType = CharacterVisionType.DALL_E_3;
        Artwork charcter1Artwork = new Artwork("Test Artwork", new URL("https://test.png"));
        URL character1Url = new URL("https://test.png");
        String character1Name = "멍멍이";
        String character1BasePrompt = "This is a base prompt";
        Character character1 = new Character(character1VisionType, charcter1Artwork, character1Name, character1Url, character1BasePrompt);


        CharacterVisionType character2VisionType = CharacterVisionType.STABLE_DIFFUSION;
        Artwork charcter2Artwork = new Artwork("Test2 Artwork", new URL("https://test.png"));
        URL character2Url = new URL("https://test2.png");
        String character2Name = "멍멍이2";
        String character2BasePrompt = "This is a base prompt2";
        Character character2 = new Character(character2VisionType, charcter2Artwork, character2Name, character2Url, character2BasePrompt);

        when(characterRepository.findAll()).thenReturn(List.of(character1, character2));

        // when
        CharacterResponses characterResponses = characterService.showCharacters();

        // then
        assertThat(characterResponses.characterResponses()).hasSize(2);
        assertThat(characterResponses.characterResponses())
                .extracting(CharacterResponse::characterVisionType)
                .containsExactly(
                        character1VisionType.toString(),
                        character2VisionType.toString()
                );
        assertThat(characterResponses.characterResponses())
                .extracting(CharacterResponse::artworkTitle)
                .containsExactly(
                        charcter1Artwork.getTitle(),
                        charcter2Artwork.getTitle()
                );
        assertThat(characterResponses.characterResponses())
                .extracting(CharacterResponse::artworkThumnailUrl)
                .containsExactly(
                        charcter1Artwork.getThumbnailUrl(),
                        charcter2Artwork.getThumbnailUrl()
                );
        assertThat(characterResponses.characterResponses())
                .extracting(CharacterResponse::name)
                .containsExactly(
                        character1Name,
                        character2Name
                );
        assertThat(characterResponses.characterResponses())
                .extracting(CharacterResponse::selectionThumbnailUrl)
                .containsExactly(
                        character1Url,
                        character2Url
                );
        verify(characterRepository, times(1)).findAll();
    }

    @DisplayName("캐릭터를 수정한다.")
    @Test
    void modifyCharacterSuccessfully() throws MalformedURLException {
        // given
        Artwork artwork = new Artwork("Test Artwork", new URL("https://test.png"));
        Character character = new Character(CharacterVisionType.DALL_E_3, artwork, "멍멍이", new URL("https://멍멍이-dalle3.png"), "This is a base prompt");
        Long characterId = 1L;

        String updateCharacterVisionType = "STABLE_DIFFUSION";
        Long updateArtworkId = 2L;
        Artwork updateArtwork = new Artwork("update Artwork", new URL("https://updateTest.png"));
        String updateCharacterName = "바뀐멍멍이";
        URL updateUrl = new URL("https://test.png");
        String updatedBasePrompt = "This is a base prompt";
        ModifyCharacterRequest request = new ModifyCharacterRequest(updateCharacterVisionType, updateArtworkId, updateCharacterName, updateUrl, updatedBasePrompt);

        // when
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));
        when(artworkService.findById(request.artworkId())).thenReturn(updateArtwork);
        characterService.modifyCharacter(characterId, request);
        Character updatedCharacter = characterService.findById(characterId);

        // then
        assertThat(updatedCharacter.getCharacterVisionType()).isEqualTo(CharacterVisionType.valueOf(updateCharacterVisionType));
        assertThat(updatedCharacter.getName()).isEqualTo(updateCharacterName);
        assertThat(updatedCharacter.getSelectionThumbnailUrl()).isEqualTo(updateUrl);
        assertThat(updatedCharacter.getBasePrompt()).isEqualTo(updatedBasePrompt);
        assertThat(updatedCharacter.getArtwork()).isEqualTo(updateArtwork);
        verify(characterRepository, times(1)).save(character);
    }

    @DisplayName("존재하지 않는 캐릭터는 수정하지 못한다.")
    @Test
    void throwExceptionWhenUpdateNotFoundCharacter() throws MalformedURLException {
        // given
        Long notFoundCharacterId = -1L;
        String updateCharacterVisionType = "STABLE_DIFFUSION";
        Long updateArtworkId = 2L;
        String updateCharacterName = "바뀐멍멍이";
        URL updateUrl = new URL("https://test.png");
        String updatedBasePrompt = "This is a base prompt";
        ModifyCharacterRequest request = new ModifyCharacterRequest(updateCharacterVisionType, updateArtworkId, updateCharacterName, updateUrl, updatedBasePrompt);


        // when & then
        when(characterRepository.findById(notFoundCharacterId)).thenReturn(Optional.empty());
        assertThrows(CharacterNoSuchElementException.class, () -> characterService.modifyCharacter(notFoundCharacterId, request));
        verify(characterRepository, times(1)).findById(notFoundCharacterId);
    }

    @DisplayName("캐릭터를 삭제한다.")
    @Test
    void deleteCharacterSuccessfully() throws MalformedURLException {
        // given
        Long characterId = 1L;
        String basePrompt = "This is a base prompt";
        Character character = new Character(CharacterVisionType.DALL_E_3, new Artwork("Test Artwork", new URL("https://test.png")), "멍멍이", new URL("https://멍멍이-dalle3.png"), basePrompt);

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));

        // when
        characterService.deleteCharacter(characterId);
        when(characterRepository.findById(characterId)).thenReturn(Optional.empty());

        // then
        verify(characterRepository, times(1)).delete(character);

        Optional<Character> deletedCharacter = characterRepository.findById(characterId);
        assertTrue(deletedCharacter.isEmpty());
    }

    @DisplayName("존재하지 않는 캐릭터는 삭제하지 못한다.")
    @Test
    void throwExceptionWhenDeleteNotFoundCharacter() throws MalformedURLException {
        // given
        Long notFoundCharacterId = -1L;
        when(characterRepository.findById(notFoundCharacterId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CharacterNoSuchElementException.class, () -> characterService.deleteCharacter(notFoundCharacterId));
        verify(characterRepository, times(1)).findById(notFoundCharacterId);
    }
}
