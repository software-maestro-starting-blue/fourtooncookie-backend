package com.startingblue.fourtooncookie.vision.reply.dto;

public record VisionReplyRequest(Long diaryId, byte[] image, Integer gridPosition) {
}
