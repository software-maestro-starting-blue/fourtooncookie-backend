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
    void shouldAddCharacterSuccessfully() throws MalformedURLException {
        // given
        AddCharacterRequest request = new AddCharacterRequest("DALL_E_3", 1L, "멍멍이", new URL("https://멍멍이-dalle3.png"), "This is a base prompt");

        ModelType modelType = ModelType.valueOf(request.modelType());
        Artwork artwork = new Artwork("Test Artwork", new URL("https://test.png"));
        when(artworkService.findById(request.artworkId())).thenReturn(artwork);

        Character character = new Character(modelType, artwork, request.name(), request.selectionThumbnailUrl(), request.basePrompt());
        when(characterRepository.save(any(Character.class))).thenReturn(character);

        // when
        characterService.addCharacter(request);

        // then
        assertThat(character.getModelType()).isEqualTo(modelType);
        assertThat(character.getArtwork()).isEqualTo(artwork);
        assertThat(character.getName()).isEqualTo(request.name());
        assertThat(character.getSelectionThumbnailUrl()).isEqualTo(request.selectionThumbnailUrl());
        assertThat(character.getBasePrompt()).isEqualTo(request.basePrompt());
        verify(characterRepository, times(1)).save(any(Character.class));
    }

    @DisplayName("ID로 캐릭터를 조회한다.")
    @Test
    void shouldFindCharacterById() throws MalformedURLException {
        // given
        Long characterId = 1L;
        String basePrompt = "This is a base prompt";
        Character character = new Character(ModelType.DALL_E_3, new Artwork("Test Artwork", new URL("https://test.png")), "멍멍이", new URL("https://멍멍이-dalle3.png"), basePrompt);
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
    void shouldThrowExceptionWhenCharacterNotFoundById() {
        // given
        Long characterId = 1L;
        when(characterRepository.findById(characterId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CharacterNoSuchElementException.class, () -> characterService.findById(characterId));
        verify(characterRepository, times(1)).findById(characterId);
    }

    @DisplayName("저장된 모든 캐릭터를 조회한다.")
    @Test
    void shouldShowAllCharacters() throws MalformedURLException {
        // given
        String basePrompt = "This is a base prompt";
        Character character1 = new Character(ModelType.DALL_E_3, new Artwork("Test Artwork", new URL("https://test.png")), "멍멍이", new URL("https://멍멍이-dalle3.png"), basePrompt);
        Character character2 = new Character(ModelType.DALL_E_3, new Artwork("Test Artwork", new URL("https://test.png")), "나비", new URL("https://나비-dalle3.png"), basePrompt);

        when(characterRepository.findAll()).thenReturn(List.of(character1, character2));

        // when
        CharacterResponses characterResponses = characterService.showCharacters();

        // then
        assertThat(characterResponses.characterResponses()).hasSize(2);
        assertThat(characterResponses.characterResponses())
                .extracting(CharacterResponse::selectionThumbnailUrl)
                .containsExactlyInAnyOrder(
                        new URL("https://멍멍이-dalle3.png"),
                        new URL("https://나비-dalle3.png")
                );
        verify(characterRepository, times(1)).findAll();
    }

    @DisplayName("캐릭터를 수정한다.")
    @Test
    void shouldModifyCharacterSuccessfully() throws MalformedURLException {
        // given
        ModifyCharacterRequest request = new ModifyCharacterRequest("STABLE_DIFFUSION", 1L, "바뀐멍멍이", new URL("https://바뀐멍멍이.png"), "Updated prompt");
        Character character = new Character(ModelType.DALL_E_3, new Artwork("Test Artwork", new URL("https://test.png")), "멍멍이", new URL("https://멍멍이-dalle3.png"), "This is a base prompt");
        Long characterId = 1L;

        Artwork artwork = new Artwork("Test Artwork", new URL("https://test.png"));
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));
        when(artworkService.findById(request.artworkId())).thenReturn(artwork);

        // when
        characterService.modifyCharacter(characterId, request);

        // then
        assertThat(character.getCharacterVisionType()).isEqualTo(CharacterVisionType.STABLE_DIFFUSION);
        assertThat(character.getName()).isEqualTo("바뀐멍멍이");
        assertThat(character.getSelectionThumbnailUrl()).isEqualTo(new URL("https://바뀐멍멍이.png"));
        assertThat(character.getBasePrompt()).isEqualTo("Updated prompt");
        assertThat(character.getArtwork()).isEqualTo(artwork);
        verify(characterRepository, times(1)).save(character);
    }

    @DisplayName("캐릭터를 삭제한다.")
    @Test
    void shouldDeleteCharacterSuccessfully() throws MalformedURLException {
        // given
        Long characterId = 1L;
        String basePrompt = "This is a base prompt";
        Character character = new Character(ModelType.DALL_E_3, new Artwork("Test Artwork", new URL("https://test.png")), "멍멍이", new URL("https://멍멍이-dalle3.png"), basePrompt);
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));

        // when
        characterService.deleteCharacter(characterId);

        // then
        verify(characterRepository, times(1)).deleteById(characterId);
    }
}
