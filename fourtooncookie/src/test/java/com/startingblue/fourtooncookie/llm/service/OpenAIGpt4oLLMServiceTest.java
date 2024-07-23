package com.startingblue.fourtooncookie.llm.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenAIGpt4oLLMServiceTest {

    @Autowired
    private OpenAIGpt4oLLMService openAIGpt4oLLMService;

    @Test
    @DisplayName("OpenAI GPT-4o와 통신이 잘 되는가를 확인한다.")
    void testOpenAiTest() {
        // given
        String systemPrompt = "The following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly.";
        String userPrompt = "What is human life expectancy in the United States?";

        // when
        String result = openAIGpt4oLLMService.getLLMResult(systemPrompt, userPrompt);

        // then
        assertNotNull(result);

        // 확인
        System.out.println("result = " + result);
    }

}