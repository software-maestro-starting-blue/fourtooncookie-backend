package com.startingblue.fourtooncookie.aws.lambda.diaryimagegenerationpayload;

public record DiaryImageGenerationCharacterPayload(Long id, String name, String visionType, String basePrompt) {
}
