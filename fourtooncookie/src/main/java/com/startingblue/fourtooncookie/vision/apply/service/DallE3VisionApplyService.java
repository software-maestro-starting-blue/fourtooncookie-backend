package com.startingblue.fourtooncookie.vision.apply.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.converter.ByteArrayToPngBufferedImageConverter;
import com.startingblue.fourtooncookie.converter.OneBufferedImageToFourSubImagesConverter;
import com.startingblue.fourtooncookie.vision.reply.dto.VisionReplyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.*;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DallE3VisionApplyService implements VisionApplyService {

    @Value("${prompt.vision.dalle3}")
    private String SYSTEM_PROMPT;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final OpenAiImageModel openAiImageModel;

    private final ByteArrayToPngBufferedImageConverter byteArrayToPngBufferedImageConverter;

    private final OneBufferedImageToFourSubImagesConverter oneBufferedImageToFourSubImagesConverter;

    @Override
    public void processVisionApply(Long diaryId, List<String> contents, Character character) {
        String userPrompt = String.join(" ", contents);
        String prompt = SYSTEM_PROMPT + userPrompt;

        String imageB64Json = getImageFromDallE3(prompt);

        List<byte[]> imageByteArrays = convertImageB64JsonToFourImagesOfByteArray(imageB64Json);

        for (int i = 0; i < 4; i++) {
            applicationEventPublisher.publishEvent(new VisionReplyEvent(diaryId, imageByteArrays.get(i), i));
        }
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
        byte[] decodedBytes = Base64.getDecoder().decode(imageB64Json);

        BufferedImage image = byteArrayToPngBufferedImageConverter.convertByteArrayToBufferedImage(decodedBytes);

        List<BufferedImage> subImages = oneBufferedImageToFourSubImagesConverter.splitImageToSubImages(image);

        return subImages.stream().map(byteArrayToPngBufferedImageConverter::convertBufferedImageToByteArray).toList();
    }

    @Override
    public CharacterVisionType getModelType() {
        return CharacterVisionType.DALL_E_3;
    }
}
