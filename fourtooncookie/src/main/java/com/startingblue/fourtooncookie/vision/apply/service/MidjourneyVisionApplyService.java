package com.startingblue.fourtooncookie.vision.apply.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.ModelType;
import com.startingblue.fourtooncookie.discord.service.midjourney.MidjourneyDiscordService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MidjourneyVisionApplyService implements VisionApplyService {

    private final MidjourneyDiscordService midjourneyDiscordService;


    @Override
    public void processVisionApply(Long diaryId, List<String> contents, Character character) {

        for (int i = 0; i < 4; i++) {
            String content = contents.get(i);

            String contentPrompt = generateContentPrompt(content);

            String prompt = getCharacterPrompt(character) + ", " + contentPrompt;

            midjourneyDiscordService.pushReadyQueue(diaryId, prompt, i, character);
        }

    }

    private String generateContentPrompt(String content) {
        return null; // TODO: LLMService를 활용하여서 Content를 Midjourney에서 활용할 수 있도록 재가공하기
    }

    private String getCharacterPrompt(Character character) {
        return null; // TODO: 캐릭터에 맞는 프롬프트를 가지고 오기
    }

    @Override
    public ModelType getModelType() {
        return ModelType.MIDJOURNEY;
    }
}
