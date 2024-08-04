package com.startingblue.fourtooncookie.llm.service;

import com.startingblue.fourtooncookie.llm.exception.LLMException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OpenAIGpt4oLLMServiceTest {

    @Mock
    private OpenAiChatModel openAiChatModel;

    @InjectMocks
    private OpenAIGpt4oLLMService openAIGpt4oLLMService;

    @Test
    @DisplayName("OpenAI API 호출이 성공적으로 응답을 반환한다.")
    void testGetLLMResult_Success() {
        // given
        String systemPrompt = "This is a system prompt.";
        String userPrompt = "This is a user prompt.";
        String expectedResponse = "This is a response.";
        ChatResponse response = new ChatResponse(List.of(new Generation(expectedResponse)));
        when(openAiChatModel.call(any(Prompt.class))).thenReturn(response);

        // when
        String actualResponse = openAIGpt4oLLMService.getLLMResult(systemPrompt, userPrompt);

        // then
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("OpenAI API 호출 시 예외가 발생하면 LLMException을 반환한다.")
    void testGetLLMResult_Exception() {
        // given
        String systemPrompt = "This is a system prompt.";
        String userPrompt = "This is a user prompt.";
        when(openAiChatModel.call(any(Prompt.class))).thenThrow(new RuntimeException("API Error"));

        // when & then
        assertThrows(LLMException.class, () -> openAIGpt4oLLMService.getLLMResult(systemPrompt, userPrompt));
    }

    @Test
    @DisplayName("GPT-4o 모델이 올바르게 설정되는지 확인한다.")
    void testGetOpenAiChatOptions() {
        // given
        OpenAiChatOptions options = openAIGpt4oLLMService.getOpenAiChatOptions();

        // when & then
        assertEquals(OpenAiApi.ChatModel.GPT_4_O.value, options.getModel().toString());
    }
}
