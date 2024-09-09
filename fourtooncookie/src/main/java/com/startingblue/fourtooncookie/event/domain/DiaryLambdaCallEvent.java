package com.startingblue.fourtooncookie.event.domain;

import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.diary.domain.Diary;

public record DiaryLambdaCallEvent(Diary diary, Character character) {
}
