package com.startingblue.fourtooncookie.vision.apply.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.ModelType;
import com.startingblue.fourtooncookie.vision.reply.dto.VisionReplyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.*;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DallE3VisionApplyService implements VisionApplyService {

    @Value("${vision.prompt.dalle3}")
    private String SYSTEM_PROMPT;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final OpenAiImageModel openAiImageModel;

    @Override
    public void processVisionApply(Long diaryId, List<String> contents, Character character) {
        String userPrompt = String.join(" ", contents);
        String prompt = SYSTEM_PROMPT + userPrompt;

        String imageB64Json = getImageFromDallE3(prompt);

        List<byte[]> imageByteArrays = convertImageB64JsonToFourImagesOfByteArray(imageB64Json);

        applicationEventPublisher.publishEvent(new VisionReplyEvent(diaryId, imageByteArrays));
    }

    private String getImageFromDallE3(String prompt) {
        OpenAiImageOptions options = OpenAiImageOptions.builder()
                .withWidth(1024)
                .withHeight(1024)
                .withN(1)
                .withResponseFormat("b64_json")
                .withModel("dall-e-3")
                .withQuality("standard")
                .build();

        ImageResponse response = openAiImageModel.call(new ImagePrompt(prompt, options));

        return response.getResult().getOutput().getB64Json();
    }

    private List<byte[]> convertImageB64JsonToFourImagesOfByteArray(String imageB64Json) {
        return null;
    }

    @Override
    public ModelType getModelType() {
        return ModelType.DALL_E_3;
    }
}
