package com.startingblue.fourtooncookie.discord.model.midjourney;

public record MidjourneyDiscordQueueEntity(Long diaryId, String message, Integer gridPosition, Boolean isImageSelectionProcessed) {
}
