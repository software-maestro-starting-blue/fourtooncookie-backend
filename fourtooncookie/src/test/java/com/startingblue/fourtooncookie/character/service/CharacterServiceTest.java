package com.startingblue.fourtooncookie.character.service;

import com.startingblue.fourtooncookie.artwork.domain.Artwork;
import com.startingblue.fourtooncookie.artwork.service.ArtworkService;
import com.startingblue.fourtooncookie.character.CharacterService;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.CharacterRepository;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.global.domain.PaymentType;
import com.startingblue.fourtooncookie.character.dto.request.CharacterSaveRequest;
import com.startingblue.fourtooncookie.character.dto.request.CharacterUpdateRequest;
import com.startingblue.fourtooncookie.character.dto.response.CharacterSavedResponse;
import com.startingblue.fourtooncookie.character.dto.response.CharacterSavedResponses;
import com.startingblue.fourtooncookie.character.exception.CharacterNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class CharacterServiceTest {

    @Mock
    MessageSource messageSource;

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
        CharacterSaveRequest request = new CharacterSaveRequest(CharacterVisionType.DALL_E_3, PaymentType.FREE, 1L, "멍멍이", new URL("https://멍멍이-dalle3.png"), "This is a base prompt");

        CharacterVisionType characterVisionType = CharacterVisionType.valueOf(request.characterVisionType().name());
        Artwork artwork = new Artwork("Test Artwork", new URL("https://test.png"));
        Character character = Character.builder()
                .characterVisionType(characterVisionType)
                .paymentType(PaymentType.FREE)
                .artwork(artwork)
                .name(request.name())
                .selectionThumbnailUrl(request.selectionThumbnailUrl())
                .basePrompt(request.basePrompt())
                .build();

        when(artworkService.readById(request.artworkId())).thenReturn(artwork);
        when(characterRepository.save(any(Character.class))).thenReturn(character);

        // when
        characterService.createCharacter(request);

        // then
        assertThat(character.getCharacterVisionType()).isEqualTo(characterVisionType);
        assertThat(character.getPaymentType()).isEqualTo(PaymentType.FREE);
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
        Character character = Character.builder()
                .characterVisionType(CharacterVisionType.DALL_E_3)
                .paymentType(PaymentType.FREE)
                .artwork(new Artwork("artwork", new URL("https://test.png")))
                .name("멍멍이")
                .selectionThumbnailUrl(new URL("https://멍멍이-dalle3.png"))
                .basePrompt(basePrompt)
                .build();
        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));

        // when
        Character foundCharacter = characterService.readById(characterId);

        // then
        assertThat(foundCharacter).isNotNull();
        assertThat(foundCharacter.getCharacterVisionType()).isEqualTo(CharacterVisionType.DALL_E_3);
        assertThat(foundCharacter.getPaymentType()).isEqualTo(PaymentType.FREE);
        assertThat(foundCharacter.getArtwork().getTitle()).isEqualTo("artwork");
        assertThat(foundCharacter.getArtwork().getThumbnailUrl()).isEqualTo(new URL("https://test.png"));
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
        assertThrows(CharacterNotFoundException.class, () -> characterService.readById(notFoundCharacterId));
        verify(characterRepository, times(1)).findById(notFoundCharacterId);
    }

    @DisplayName("저장된 모든 캐릭터를 조회한다.")
    @Test
    void showAllCharacters() throws MalformedURLException {
        // given
        Character character1 = Character.builder()
                .id(1L)
                .characterVisionType(CharacterVisionType.DALL_E_3)
                .paymentType(PaymentType.FREE)
                .artwork(new Artwork("Test Artwork", new URL("https://test.png")))
                .name("랜덤")
                .selectionThumbnailUrl(new URL("https://test.png"))
                .basePrompt("This is a base prompt")
                .build();

        Character character2 = Character.builder()
                .id(2L)
                .characterVisionType(CharacterVisionType.STABLE_DIFFUSION)
                .paymentType(PaymentType.FREE)
                .artwork(new Artwork("Test2 Artwork", new URL("https://test2.png")))
                .name("말랑")
                .selectionThumbnailUrl(new URL("https://test2.png"))
                .basePrompt("This is a base prompt2")
                .build();

        when(messageSource.getMessage(eq("character.name." + character1.getId()), any(), any())).thenReturn(character1.getName());
        when(messageSource.getMessage(eq("character.name." + character2.getId()), any(), any())).thenReturn(character2.getName());
        when(characterRepository.findAll()).thenReturn(List.of(character1, character2));

        // when
        CharacterSavedResponses characterSavedResponses = CharacterSavedResponses.of(characterService.readAllCharacters(Locale.KOREAN));

        // then
        assertThat(characterSavedResponses.characterSavedResponses()).hasSize(2);
        assertThat(characterSavedResponses.characterSavedResponses())
                .extracting(CharacterSavedResponse::paymentType)
                .containsExactly(
                        character1.getPaymentType().toString(),
                        character2.getPaymentType().toString()
                );
        assertThat(characterSavedResponses.characterSavedResponses())
                .extracting(CharacterSavedResponse::artworkTitle)
                .containsExactly(
                        character1.getArtwork().getTitle(),
                        character2.getArtwork().getTitle()
                );
        assertThat(characterSavedResponses.characterSavedResponses())
                .extracting(CharacterSavedResponse::artworkThumbnailUrl)
                .containsExactly(
                        character1.getArtwork().getThumbnailUrl(),
                        character2.getArtwork().getThumbnailUrl()
                );
        assertThat(characterSavedResponses.characterSavedResponses())
                .extracting(CharacterSavedResponse::name)
                .containsExactly(
                        character1.getName(),
                        character2.getName()
                );
        assertThat(characterSavedResponses.characterSavedResponses())
                .extracting(CharacterSavedResponse::selectionThumbnailUrl)
                .containsExactly(
                        character1.getSelectionThumbnailUrl(),
                        character2.getSelectionThumbnailUrl()
                );
        verify(characterRepository, times(1)).findAll();
    }

    @DisplayName("캐릭터를 수정한다.")
    @Test
    void updateCharacterSuccessfully() throws MalformedURLException {
        // given
        Artwork artwork = new Artwork("Test Artwork", new URL("https://test.png"));
        Character character = Character.builder()
                .characterVisionType(CharacterVisionType.DALL_E_3)
                .paymentType(PaymentType.FREE)
                .artwork(artwork)
                .name("멍멍이")
                .selectionThumbnailUrl(new URL("https://멍멍이-dalle3.png"))
                .basePrompt("This is a base prompt")
                .build();
        Long characterId = 1L;

        CharacterVisionType updateCharacterVisionType = CharacterVisionType.STABLE_DIFFUSION;
        Long updateArtworkId = 2L;
        Artwork updateArtwork = new Artwork("updateById Artwork", new URL("https://updateTest.png"));
        String updateCharacterName = "바뀐멍멍이";
        URL updateUrl = new URL("https://test.png");
        String updatedBasePrompt = "This is a base prompt";
        CharacterUpdateRequest request = new CharacterUpdateRequest(updateCharacterVisionType, PaymentType.PAID, updateArtworkId, updateCharacterName, updateUrl, updatedBasePrompt);

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));
        when(artworkService.readById(request.artworkId())).thenReturn(updateArtwork);

        // when
        characterService.updateCharacter(characterId, request);
        Character updatedCharacter = characterService.readById(characterId);

        // then
        assertThat(updatedCharacter.getCharacterVisionType()).isEqualTo(updateCharacterVisionType);
        assertThat(updatedCharacter.getName()).isEqualTo(updateCharacterName);
        assertThat(updatedCharacter.getPaymentType()).isEqualTo(PaymentType.PAID);
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
        CharacterVisionType updateCharacterVisionType = CharacterVisionType.STABLE_DIFFUSION;
        Long updateArtworkId = 2L;
        String updateCharacterName = "바뀐멍멍이";
        URL updateUrl = new URL("https://test.png");
        String updatedBasePrompt = "This is a base prompt";
        CharacterUpdateRequest request = new CharacterUpdateRequest(updateCharacterVisionType, PaymentType.FREE, updateArtworkId, updateCharacterName, updateUrl, updatedBasePrompt);

        when(characterRepository.findById(notFoundCharacterId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CharacterNotFoundException.class, () -> characterService.updateCharacter(notFoundCharacterId, request));
        verify(characterRepository, times(1)).findById(notFoundCharacterId);
    }

    @DisplayName("캐릭터를 삭제한다.")
    @Test
    void deleteCharacterSuccessfully() throws MalformedURLException {
        // given
        Long characterId = 1L;
        String basePrompt = "This is a base prompt";
        Character character = Character.builder()
                .characterVisionType(CharacterVisionType.DALL_E_3)
                .paymentType(PaymentType.FREE)
                .artwork(new Artwork("Test Artwork", new URL("https://test.png")))
                .name("멍멍이")
                .selectionThumbnailUrl(new URL("https://멍멍이-dalle3.png"))
                .basePrompt(basePrompt)
                .build();

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
    void throwExceptionWhenDeleteNotFoundCharacter() {
        // given
        Long notFoundCharacterId = -1L;
        when(characterRepository.findById(notFoundCharacterId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CharacterNotFoundException.class, () -> characterService.deleteCharacter(notFoundCharacterId));
        verify(characterRepository, times(1)).findById(notFoundCharacterId);
    }
}
