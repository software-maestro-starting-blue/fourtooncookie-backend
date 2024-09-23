package com.startingblue.fourtooncookie.aws.lambda.diaryimagegenerationpayload;

public record DiaryImageGenerationLambdaPayload(Long diaryId, String content, DiaryImageGenerationCharacterPayload character) {

}
