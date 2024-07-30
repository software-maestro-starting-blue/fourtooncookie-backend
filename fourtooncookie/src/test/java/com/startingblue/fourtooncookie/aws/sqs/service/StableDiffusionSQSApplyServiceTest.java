package com.startingblue.fourtooncookie.aws.sqs.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StableDiffusionSQSApplyServiceTest {

    @Autowired
    StableDiffusionSQSApplyService stableDiffusionSQSApplyService;

    //@Test
    @DisplayName("sendMessage 실제 테스트")
    void sendMessageTest() throws Exception {
        // given
        Long diaryId = 1L;
        String prompt = "prompt";
        Integer gridPosition = 1;

        Constructor<Character> constructor = Character.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Character character = constructor.newInstance();

        Field privateField = Character.class.getDeclaredField("name");
        privateField.setAccessible(true);
        privateField.set(character, "characterName");

        // when
        stableDiffusionSQSApplyService.sendMessage(diaryId, prompt, character, gridPosition);

        // then
    }

}