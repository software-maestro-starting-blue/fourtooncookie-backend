package com.startingblue.fourtooncookie.llm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.List;

@RequiredArgsConstructor
abstract public class OpenAILLMService implements LLMService {

    private final OpenAiChatModel openAiChatModel;

    @Override
    public String getLLMResult(String systemPrompt, String userPrompt) {
        OpenAiChatOptions option = getOpenAiChatOptions();

        ChatResponse response = openAiChatModel.call(new Prompt(List.of(new SystemMessage(systemPrompt), new UserMessage(userPrompt)), option));

        return response.getResults().get(0).getOutput().getContent();
    }

    abstract public OpenAiChatOptions getOpenAiChatOptions();

}
