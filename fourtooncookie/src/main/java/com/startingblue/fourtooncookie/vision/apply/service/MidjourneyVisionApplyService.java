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
        // TODO: 각 컨텐츠에 대한 프롬프트 재가공
        // TODO: MidjourneyDiscordService의 pushReadyQueue를 실행하여 정보 전달
    }

    @Override
    public ModelType getModelType() {
        return ModelType.MIDJOURNEY;
    }
}
