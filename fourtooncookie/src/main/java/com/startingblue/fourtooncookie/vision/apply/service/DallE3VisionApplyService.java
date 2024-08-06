package com.startingblue.fourtooncookie.vision.apply.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.converter.ByteArrayToPngBufferedImageConverter;
import com.startingblue.fourtooncookie.converter.OneBufferedImageToFourSubImagesConverter;
import com.startingblue.fourtooncookie.vision.reply.dto.VisionReplyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DallE3VisionApplyService implements VisionApplyService {

    @Value("${prompt.vision.dalle3}")
    private String systemPrompt;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final OpenAiImageModel openAiImageModel;
    private final ByteArrayToPngBufferedImageConverter byteArrayToPngBufferedImageConverter;
    private final OneBufferedImageToFourSubImagesConverter oneBufferedImageToFourSubImagesConverter;

    private static final int IMAGE_WIDTH = 1024;
    private static final int IMAGE_HEIGHT = 1024;
    private static final int IMAGE_COUNT = 1;
    private static final String RESPONSE_FORMAT = "b64_json";
    private static final String MODEL_NAME = "dall-e-3";
    private static final String IMAGE_QUALITY = "standard";

    @Override
    public void processVisionApply(Long diaryId, List<String> contents, Character character) {
        String prompt = buildPrompt(contents);
        String imageB64Json = getImageFromDallE3(prompt);
        List<byte[]> imageByteArrays = convertImageB64JsonToFourImagesOfByteArray(imageB64Json);
        publishVisionReplyEvents(diaryId, imageByteArrays);
    }

    private String buildPrompt(List<String> contents) {
        String memberPrompt = String.join(" ", contents);
        return systemPrompt + memberPrompt;
    }

    private String getImageFromDallE3(String prompt) {
        OpenAiImageOptions options = createImageOptions();
        ImageResponse response = openAiImageModel.call(new ImagePrompt(prompt, options));
        return response.getResult().getOutput().getB64Json();
    }

    private OpenAiImageOptions createImageOptions() {
        return OpenAiImageOptions.builder()
                .withWidth(IMAGE_WIDTH)
                .withHeight(IMAGE_HEIGHT)
                .withN(IMAGE_COUNT)
                .withResponseFormat(RESPONSE_FORMAT)
                .withModel(MODEL_NAME)
                .withQuality(IMAGE_QUALITY)
                .build();
    }

    private List<byte[]> convertImageB64JsonToFourImagesOfByteArray(String imageB64Json) {
        byte[] decodedBytes = Base64.getDecoder().decode(imageB64Json);
        BufferedImage image = byteArrayToPngBufferedImageConverter.convertByteArrayToBufferedImage(decodedBytes);
        List<BufferedImage> subImages = oneBufferedImageToFourSubImagesConverter.splitImageToSubImages(image);

        return subImages.stream()
                .map(byteArrayToPngBufferedImageConverter::convertBufferedImageToByteArray)
                .collect(Collectors.toList());
    }

    private void publishVisionReplyEvents(Long diaryId, List<byte[]> imageByteArrays) {
        for (int i = 0; i < imageByteArrays.size(); i++) {
            applicationEventPublisher.publishEvent(new VisionReplyEvent(diaryId, imageByteArrays.get(i), i));
        }
    }

    @Override
    public CharacterVisionType getVisionType() {
        return CharacterVisionType.DALL_E_3;
    }
}
