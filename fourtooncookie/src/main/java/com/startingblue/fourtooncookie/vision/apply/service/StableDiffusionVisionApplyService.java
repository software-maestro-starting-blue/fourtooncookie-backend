package com.startingblue.fourtooncookie.vision.apply.service;

import com.startingblue.fourtooncookie.aws.sqs.service.StableDiffusionSQSApplyService;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;
import com.startingblue.fourtooncookie.llm.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StableDiffusionVisionApplyService implements VisionApplyService {

    private final StableDiffusionSQSApplyService stableDiffusionSQSApplyService;

    private final LLMService llmService;


    @Override
    public void processVisionApply(Long diaryId, List<String> contents, Character character) {
        for (int i = 0; i < 4; i++) {
            String content = contents.get(i);
            String refinedPrompt = getRefinedPrompt(content, character);
            stableDiffusionSQSApplyService.sendMessage(diaryId, refinedPrompt, character, i);
        }
    }

    private String getRefinedPrompt(String content, Character character) {
        return null; //TODO: 프롬프트를 LLM을 통해 정제하고 캐릭터의 특성을 넣는 로직
    }

    @Override
    public CharacterVisionType getModelType() {
        return CharacterVisionType.STABLE_DIFFUSION;
    }
}
