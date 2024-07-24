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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DallE3VisionApplyService implements VisionApplyService {

    @Value("${prompt.vision.dalle3}")
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
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(imageB64Json);

            ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
            BufferedImage originalImage = ImageIO.read(bais);

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            BufferedImage[] subImages = new BufferedImage[4];

            subImages[0] = originalImage.getSubimage(0, 0, width / 2, height / 2); // top-left
            subImages[1] = originalImage.getSubimage(width / 2, 0, width / 2, height / 2); // top-right
            subImages[2] = originalImage.getSubimage(0, height / 2, width / 2, height / 2); // bottom-left
            subImages[3] = originalImage.getSubimage(width / 2, height / 2, width / 2, height / 2); // bottom-right


            List<byte[]> subImageBytes = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(subImages[i], "png", baos);
                subImageBytes.add(baos.toByteArray());
            }

            return subImageBytes;
        } catch (IOException exception) {
            throw new IllegalStateException("이미지를 4장으로 나누는 과정에서 오류가 나타남", exception);
        }
    }

    @Override
    public ModelType getModelType() {
        return ModelType.DALL_E_3;
    }
}
