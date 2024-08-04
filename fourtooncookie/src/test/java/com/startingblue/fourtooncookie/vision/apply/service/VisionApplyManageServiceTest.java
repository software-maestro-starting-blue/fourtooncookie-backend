package com.startingblue.fourtooncookie.vision.apply.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.llm.service.LLMService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static com.startingblue.fourtooncookie.character.domain.CharacterVisionType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.invokeMethod;

@ExtendWith(MockitoExtension.class)
public class VisionApplyManageServiceTest {

    @Mock
    private LLMService llmService;

    @Mock
    private VisionApplyService visionApplyService;

    @InjectMocks
    private VisionApplyManageService visionApplyManageService;

    private final String contentSplitSystemPrompt = "Test System Prompt: ";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(visionApplyManageService, "contentSplitSystemPrompt", contentSplitSystemPrompt);
        ReflectionTestUtils.setField(visionApplyManageService, "visionApplyServices", List.of(visionApplyService));
    }

    @Test
    @DisplayName("다이어리로 이미지 생성 성공")
    void testCreateImageByDiary() {
        Long diaryId = 1L;
        String content = "Test content";
        Character character = mock(Character.class);

        // Mock the CharacterVisionType
        when(character.getCharacterVisionType()).thenReturn(DALL_E_3);

        // Mock the VisionApplyService
        when(visionApplyService.getVisionType()).thenReturn(DALL_E_3);

        // Mock the LLMService
        String llmResult = "Sentence 1. Sentence 2. Sentence 3. Sentence 4.";
        when(llmService.getLLMResult(any(), any())).thenReturn(llmResult);

        // Call the method under test
        visionApplyManageService.createImageByDiary(diaryId, content, character);

        // Verify the processVisionApply method was called
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(visionApplyService).processVisionApply(eq(diaryId), captor.capture(), eq(character));

        // Verify the split contents
        List<String> splitContents = captor.getValue();
        assertEquals(4, splitContents.size());
        assertEquals("Sentence 1", splitContents.get(0));
        assertEquals(" Sentence 2", splitContents.get(1));
        assertEquals(" Sentence 3", splitContents.get(2));
        assertEquals(" Sentence 4", splitContents.get(3));
    }

    @Test
    @DisplayName("비전 타입에 따른 서비스 찾기 성공")
    void testFindVisionApplyServiceByVisionType() {
        when(visionApplyService.getVisionType()).thenReturn(DALL_E_3);

        VisionApplyService foundService = invokeMethod(visionApplyManageService, "findVisionApplyServiceByVisionType", DALL_E_3);

        assertNotNull(foundService);
        assertEquals(DALL_E_3, foundService.getVisionType());
    }

    @Test
    @DisplayName("비전 서비스가 없을 때 예외 발생")
    void testFindVisionApplyServiceByVisionType_NoServicesAvailable() {
        ReflectionTestUtils.setField(visionApplyManageService, "visionApplyServices", Collections.emptyList());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                invokeMethod(visionApplyManageService, "findVisionApplyServiceByVisionType", STABLE_DIFFUSION)
        );

        assertEquals("No Vision Service Found for Vision Type: " + STABLE_DIFFUSION, exception.getMessage());
    }

    @Test
    @DisplayName("비전 타입에 따른 서비스가 없을 때 예외 발생")
    void testFindVisionApplyServiceByVisionType_NotFound() {
        when(visionApplyService.getVisionType()).thenReturn(DALL_E_3);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            invokeMethod(visionApplyManageService, "findVisionApplyServiceByVisionType", MIDJOURNEY);
        });

        assertEquals("No Vision Service Found for Vision Type: MIDJOURNEY", exception.getMessage());
    }

    @Test
    @DisplayName("내용을 4개의 부분으로 분할 성공")
    void testSplitContentIntoParts() {
        String content = "Test content.";
        String llmResult = "Sentence 1. Sentence 2. Sentence 3. Sentence 4.";
        when(llmService.getLLMResult(any(), any())).thenReturn(llmResult);

        List<String> parts = invokeMethod(visionApplyManageService, "splitContentIntoParts", content);

        assertNotNull(parts);
        assertEquals(4, parts.size());
        assertEquals("Sentence 1", parts.get(0));
        assertEquals(" Sentence 2", parts.get(1));
        assertEquals(" Sentence 3", parts.get(2));
        assertEquals(" Sentence 4", parts.get(3));
    }
}
