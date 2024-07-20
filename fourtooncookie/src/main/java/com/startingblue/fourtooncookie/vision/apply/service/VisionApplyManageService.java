package com.startingblue.fourtooncookie.vision.apply.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.ModelType;
import com.startingblue.fourtooncookie.llm.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VisionApplyManageService {

    @Value("${ai.split.prompt}")
    private String CONTENT_SPLIT_SYSTEM_PROMPT;

    private final static String RESULT_SPLIT_REGEX = "\\.";

    private final List<VisionApplyService> visionApplyServices;

    private final LLMService llmService;


    @Async
    public void createImageByDiary(Long diaryId, String content, Character character) {
        VisionApplyService visionApplyService = findVisionApplyServiceByModelType(character.getModelType());

        List<String> contents = seperateContentBy4contents(content);

        visionApplyService.processVisionApply(diaryId, contents, character);
    }

    private VisionApplyService findVisionApplyServiceByModelType(ModelType modelType) {
        return visionApplyServices.stream()
                .filter(visionApplyService -> visionApplyService.getModelType().equals(modelType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Vision Service Found"));
    }

    private List<String> seperateContentBy4contents(String content) {
        String result = llmService.getLLMResult(CONTENT_SPLIT_SYSTEM_PROMPT, content);
        return List.of(result.split(RESULT_SPLIT_REGEX));
    }

}
