package com.startingblue.fourtooncookie.llm.service;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;


@Service
public class OpenAIGpt4oLLMService extends OpenAILLMService {

    public OpenAIGpt4oLLMService(OpenAiChatModel openAiChatModel) {
        super(openAiChatModel);
    }

    @Override
    public OpenAiChatOptions getOpenAiChatOptions() {
        return OpenAiChatOptions.builder()
                .withModel(OpenAiApi.ChatModel.GPT_4_O)
                .build();
    }
}
