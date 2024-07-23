package com.startingblue.fourtooncookie.character.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.CharacterType;
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

    @InjectMocks
    private CharacterService characterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        characterRepository.deleteAllInBatch();
    }

    @DisplayName("캐릭터를 추가한다.")
    @Test
    void addCharacter() throws MalformedURLException {
        // given
        AddCharacterRequest request = new AddCharacterRequest("DALL_E_3", "멍멍이", new URL("https://멍멍이-dalle3.png"));
        Character character = new Character(CharacterType.DALL_E_3, "멍멍이", new URL("https://멍멍이-dalle3.png"));
        when(characterRepository.save(any(Character.class))).thenReturn(character);

        // when
        characterService.addCharacter(request);

        // then
        verify(characterRepository, times(1)).save(any(Character.class));
    }

    @DisplayName("ID로 캐릭터를 조회한다.")
    @Test
    void findById() throws MalformedURLException {
        // given
        Long characterId = 1L;
        Character character = new Character(CharacterType.DALL_E_3, "멍멍이", new URL("https://멍멍이-dalle3.png"));
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));

        // when
        Character foundCharacter = characterService.findById(characterId);

        // then
        assertThat(foundCharacter).isNotNull();
        assertThat(foundCharacter.getName()).isEqualTo("멍멍이");
        verify(characterRepository, times(1)).findById(characterId);
    }

    @DisplayName("존재하지 않는 캐릭터 조회시 예외를 발생시킨다.")
    @Test
    void findByIdThrowsException() {
        // given
        Long characterId = 1L;
        when(characterRepository.findById(characterId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CharacterNoSuchElementException.class, () -> characterService.findById(characterId));
        verify(characterRepository, times(1)).findById(characterId);
    }

    @DisplayName("저장된 모든 캐릭터를 조회한다.")
    @Test
    void showCharacters() throws MalformedURLException {
        // given
        Character character1 = new Character(CharacterType.DALL_E_3, "멍멍이", new URL("https://멍멍이-dalle3.png"));
        Character character2 = new Character(CharacterType.DALL_E_3, "나비", new URL("https://나비-dalle3.png"));
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
    void modifyCharacter() throws MalformedURLException {
        // given
        Long characterId = 1L;
        ModifyCharacterRequest request = new ModifyCharacterRequest("STABLE_DIFFUSION", "바뀐멍멍이", new URL("https://바뀐멍멍이.png"));
        Character character = new Character(CharacterType.DALL_E_3, "멍멍이", new URL("https://멍멍이-dalle3.png"));
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));

        // when
        characterService.modifyCharacter(characterId, request);

        // then
        assertThat(character.getCharacterType()).isEqualTo(CharacterType.STABLE_DIFFUSION);
        assertThat(character.getName()).isEqualTo("바뀐멍멍이");
        assertThat(character.getSelectionThumbnailUrl()).isEqualTo(new URL("https://바뀐멍멍이.png"));
        verify(characterRepository, times(1)).save(character);
    }

    @DisplayName("캐릭터를 삭제한다.")
    @Test
    void deleteCharacter() throws MalformedURLException {
        // given
        Character character = new Character(CharacterType.DALL_E_3, "멍멍이", new URL("https://멍멍이-dalle3.png"));
        characterRepository.save(character);
        Long characterId = character.getId();

        // when
        characterService.deleteCharacter(characterId);

        // then
        assertThat(characterRepository.existsById(characterId)).isFalse();
        verify(characterRepository, times(1)).deleteById(characterId);
    }
}
