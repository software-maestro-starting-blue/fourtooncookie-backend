package com.startingblue.fourtooncookie.vision.apply.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.converter.ByteArrayToPngBufferedImageConverter;
import com.startingblue.fourtooncookie.converter.OneBufferedImageToFourSubImagesConverter;
import com.startingblue.fourtooncookie.vision.reply.dto.VisionReplyEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;

import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DallE3VisionApplyServiceTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private OpenAiImageModel openAiImageModel;

    @Mock
    private ByteArrayToPngBufferedImageConverter byteArrayToPngBufferedImageConverter;

    @Mock
    private OneBufferedImageToFourSubImagesConverter oneBufferedImageToFourSubImagesConverter;

    @InjectMocks
    private DallE3VisionApplyService dallE3VisionApplyService;

    private final String systemPrompt = "Test System Prompt: ";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dallE3VisionApplyService, "systemPrompt", systemPrompt);
    }

    @Test
    @DisplayName("프롬프트 빌드 성공")
    void testBuildPrompt() {
        List<String> contents = List.of("Content 1", "Content 2");
        String prompt = ReflectionTestUtils.invokeMethod(dallE3VisionApplyService, "buildPrompt", contents);
        assertEquals(systemPrompt + "Content 1 Content 2", prompt);
    }

    @Test
    @DisplayName("OpenAiImageOptions 생성 성공")
    void testCreateImageOptions() {
        OpenAiImageOptions options = ReflectionTestUtils.invokeMethod(dallE3VisionApplyService, "createImageOptions");
        assertNotNull(options);
        assertEquals(1024, options.getWidth());
        assertEquals(1024, options.getHeight());
        assertEquals(1, options.getN());
        assertEquals("b64_json", options.getResponseFormat());
        assertEquals("dall-e-3", options.getModel());
        assertEquals("standard", options.getQuality());
    }

    @Test
    @DisplayName("이미지 Base64 JSON을 4개의 이미지로 변환 성공")
    void testConvertImageB64JsonToFourImagesOfByteArray() {
        String imageB64Json = Base64.getEncoder().encodeToString("test-image".getBytes());
        BufferedImage bufferedImage = mock(BufferedImage.class);
        when(byteArrayToPngBufferedImageConverter.convertToDatabaseColumn(any())).thenReturn(bufferedImage);
        when(oneBufferedImageToFourSubImagesConverter.convertToDatabaseColumn(any())).thenReturn(List.of(bufferedImage, bufferedImage, bufferedImage, bufferedImage));
        when(byteArrayToPngBufferedImageConverter.convertToEntityAttribute(any())).thenReturn("test-image".getBytes());

        List<byte[]> images = ReflectionTestUtils.invokeMethod(dallE3VisionApplyService, "convertImageB64JsonToFourImagesOfByteArray", imageB64Json);

        assertNotNull(images);
        assertEquals(4, images.size());
        for (byte[] image : images) {
            assertArrayEquals("test-image".getBytes(), image);
        }
    }

    @Test
    @DisplayName("VisionReplyEvent 발행 성공")
    void testPublishVisionReplyEvents() {
        Long diaryId = 1L;
        List<byte[]> imageByteArrays = List.of("image1".getBytes(), "image2".getBytes(), "image3".getBytes(), "image4".getBytes());

        ReflectionTestUtils.invokeMethod(dallE3VisionApplyService, "publishVisionReplyEvents", diaryId, imageByteArrays);

        ArgumentCaptor<VisionReplyEvent> eventCaptor = ArgumentCaptor.forClass(VisionReplyEvent.class);
        verify(applicationEventPublisher, times(4)).publishEvent(eventCaptor.capture());

        List<VisionReplyEvent> capturedEvents = eventCaptor.getAllValues();
        assertEquals(4, capturedEvents.size());
        for (int i = 0; i < 4; i++) {
            VisionReplyEvent event = capturedEvents.get(i);
            System.out.println("diary Id: " + event.diaryId());
            System.out.println("image: " + event.image());
            System.out.println("gridPosition: " + event.gridPosition());
            System.out.println();
            assertEquals(diaryId, event.diaryId());
            assertArrayEquals(imageByteArrays.get(i), event.image());
            assertEquals(i, event.gridPosition());
        }
    }
}
