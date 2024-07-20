package com.startingblue.fourtooncookie.vision.reply.dto;

import java.util.List;

public record VisionReplyRequest(Long diaryId, List<byte[]> images) {
}
