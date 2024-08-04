package com.startingblue.fourtooncookie.vision.apply.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.llm.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class VisionApplyManageService {

    private static final String RESULT_SPLIT_REGEX = "\\.";
    private static final int CONTENT_PARTS = 4;

    @Value("${ai.split.prompt}")
    private String contentSplitSystemPrompt;

    private final List<VisionApplyService> visionApplyServices;
    private final LLMService llmService;

    @Async
    public void applyCreateImageByDiary(Long diaryId, String content, Character character) {
        VisionApplyService visionApplyService = findVisionApplyServiceByVisionType(character.getCharacterVisionType());
        List<String> contents = splitContentIntoParts(content);
        visionApplyService.processVisionApply(diaryId, contents, character);
    }

    private VisionApplyService findVisionApplyServiceByVisionType(CharacterVisionType visionType) {
        return visionApplyServices.stream()
                .filter(service -> service.getVisionType().equals(visionType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Vision Service Found for Vision Type: " + visionType));
    }

    private List<String> splitContentIntoParts(String content) {
        String result = llmService.getLLMResult(contentSplitSystemPrompt, content);
        return Stream.of(result.split(RESULT_SPLIT_REGEX))
                .limit(CONTENT_PARTS)
                .collect(Collectors.toList());
    }
}
