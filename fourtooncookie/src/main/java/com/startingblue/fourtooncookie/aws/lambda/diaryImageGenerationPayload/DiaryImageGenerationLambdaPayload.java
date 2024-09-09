package com.startingblue.fourtooncookie.aws.lambda.diaryImageGenerationPayload;

public record DiaryImageGenerationLambdaPayload(Long id, String content, DiaryImageGenerationCharacterPayload character) {

}
