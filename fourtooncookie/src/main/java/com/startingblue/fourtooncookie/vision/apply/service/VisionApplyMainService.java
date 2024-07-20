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
public class VisionApplyMainService {

    @Value("${ai.split.prompt}")
    private String CONTENT_SPLIT_SYSTEM_PROMPT;

    private final List<VisionApplyService> visionApplyServices;

    private final LLMService llmService;


    @Async
    public void createAIImageByDiary(Long diaryId, String content, Character character) {
        VisionApplyService visionApplyService = findVisionRequestServiceByModelType(character.getModelType());

        List<String> contents = splitContentBy4(content);

        visionApplyService.processVisionApply(diaryId, contents, character);
    }

    private VisionApplyService findVisionRequestServiceByModelType(ModelType modelType) {
        Optional<VisionApplyService> visionApplyService = visionApplyServices.stream()
                .filter(nowVisionService -> nowVisionService.getModelType().equals(modelType))
                .findFirst();

        if (visionApplyService.isEmpty()) {
            throw new IllegalStateException("No Vision Service Found");
        }

        return visionApplyService.get();
    }

    private List<String> splitContentBy4(String content) {
        String result = llmService.getLLMResult(CONTENT_SPLIT_SYSTEM_PROMPT, content);
        return List.of(result.split("\\."));
    }

}
