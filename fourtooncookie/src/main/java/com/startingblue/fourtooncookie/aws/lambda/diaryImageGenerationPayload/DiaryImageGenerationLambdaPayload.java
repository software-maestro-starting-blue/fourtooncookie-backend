package com.startingblue.fourtooncookie.aws.lambda.diaryImageGenerationPayload;

public record DiaryImageGenerationLambdaPayload(Long diaryId, String content, DiaryImageGenerationCharacterPayload character) {

}
