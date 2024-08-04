package com.startingblue.fourtooncookie.llm.service;

import com.startingblue.fourtooncookie.llm.exception.LLMException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
abstract public class OpenAILLMService implements LLMService {

    private final OpenAiChatModel openAiChatModel;

    @Override
    public String getLLMResult(String systemPrompt, String userPrompt) {
        OpenAiChatOptions option = getOpenAiChatOptions();
        try {
            ChatResponse response = openAiChatModel.call(new Prompt(List.of(new SystemMessage(systemPrompt), new UserMessage(userPrompt)), option));
            return response.getResults().get(0).getOutput().getContent();
        } catch (Exception e) {
            throw new LLMException("Exception occurred while calling OpenAI API: {}", e);
        }
    }

    abstract public OpenAiChatOptions getOpenAiChatOptions();
}
