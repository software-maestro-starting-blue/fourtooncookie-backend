package com.startingblue.fourtooncookie.vision.apply.service;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.character.domain.CharacterVisionType;

import java.util.List;

public interface VisionApplyService {

    void processVisionApply(Long diaryId, List<String> contents, Character character);

    CharacterVisionType getVisionType();
}
