package com.startingblue.fourtooncookie.vision.reply.dto;

public record VisionReplyEvent(Long diaryId, byte[] image, Integer gridPosition) {
}
