package com.startingblue.fourtooncookie.vision.reply.dto;

import java.util.List;

public record VisionReplyEvent(Long diaryId, List<byte[]> images) {
}
