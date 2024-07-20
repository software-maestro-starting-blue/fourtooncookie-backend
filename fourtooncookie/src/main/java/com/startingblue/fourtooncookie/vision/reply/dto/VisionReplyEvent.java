package com.startingblue.fourtooncookie.vision.reply.dto;

import lombok.Getter;

import java.util.List;

@Getter
public record VisionReplyEvent(Long diaryId, List<byte[]> images) {
}
