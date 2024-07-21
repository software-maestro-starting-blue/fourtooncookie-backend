package com.startingblue.fourtooncookie.character.domain;

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
class CharacterRepositoryTest {

    @Autowired
    CharacterRepository characterRepository;

    @DisplayName("특정 모델 타입의 모든 캐릭터를 가져온다.")
    @Test
    void findAllByModelType() throws MalformedURLException {
        // given
        Character dogDalle3Character = new Character(ModelType.DALL_E_3, "멍멍이", new URL("https://멍멍이-dalle3.png"));
        Character catDalle3Character = new Character(ModelType.DALL_E_3, "나비", new URL("https://나비-dalle3.png"));
        Character catStableDiffusionCharacter = new Character(ModelType.STABLE_DIFFUSION, "개", new URL("http://개-stable-diffusion.png"));
        characterRepository.saveAll(List.of(dogDalle3Character, catDalle3Character, catStableDiffusionCharacter));

        // when
        List<Character> savedDalle3Characters = characterRepository.findAllByModelType(ModelType.DALL_E_3);

        // then
        assertThat(savedDalle3Characters).hasSize(2);
        assertThat(savedDalle3Characters)
                .extracting(Character::getName)
                .containsExactlyInAnyOrder("멍멍이", "나비");

        assertThat(savedDalle3Characters)
                .extracting(Character::getSelectionThumbnailUrl)
                .containsExactlyInAnyOrder(
                        new URL("https://멍멍이-dalle3.png"),
                        new URL("https://나비-dalle3.png")
                );
    }
}
