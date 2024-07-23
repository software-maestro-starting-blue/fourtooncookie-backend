package com.startingblue.fourtooncookie.vision.apply.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.ModelType;
import com.startingblue.fourtooncookie.vision.reply.dto.VisionReplyEvent;
import lombok.RequiredArgsConstructor;
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

    @Override
    public void processVisionApply(Long diaryId, List<String> contents, Character character) {
        String userPrompt = String.join(" ", contents);
        String prompt = SYSTEM_PROMPT + userPrompt;

        String imageUrl = getImageFromDallE3(prompt);

        List<byte[]> imageByteArrays = convertImageUrlToFourImages(imageUrl);

        applicationEventPublisher.publishEvent(new VisionReplyEvent(diaryId, imageByteArrays));
    }

    private String getImageFromDallE3(String prompt) {
        return null;
    }

    private List<byte[]> convertImageUrlToFourImages(String imageUrl) {
        return null;
    }

    @Override
    public ModelType getModelType() {
        return ModelType.DALL_E_3;
    }
}
