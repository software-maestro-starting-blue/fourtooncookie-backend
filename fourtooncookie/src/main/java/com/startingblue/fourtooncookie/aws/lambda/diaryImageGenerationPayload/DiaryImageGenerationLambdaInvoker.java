package com.startingblue.fourtooncookie.aws.lambda.diaryImageGenerationPayload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startingblue.fourtooncookie.aws.lambda.LambdaInvoker;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;

@Service
public class DiaryImageGenerationLambdaInvoker extends LambdaInvoker {

    private static final String FUNCTION_NAME = "fourtooncookie-diaryimage-ai-apply-lambda";

    private final ObjectMapper objectMapper;

    public DiaryImageGenerationLambdaInvoker(LambdaClient lambdaClient, ObjectMapper objectMapper1) {
        super(lambdaClient, FUNCTION_NAME, InvocationType.REQUEST_RESPONSE);
        this.objectMapper = objectMapper1;
    }

    public void invokeDiaryImageGenerationLambda(Diary diary, Character character) throws RuntimeException {
        DiaryImageGenerationLambdaPayload diaryImageGenerationLambdaPayload = buildPayload(diary, character);
        String serializePayload = serializePayload(diaryImageGenerationLambdaPayload);
        invokeLambda(serializePayload);
    }

    private String serializePayload(DiaryImageGenerationLambdaPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("페이로드 직렬화 중 오류 발생", e);
        }
    }

    private DiaryImageGenerationLambdaPayload buildPayload(Diary diary, Character character) {
        return new DiaryImageGenerationLambdaPayload(
                diary.getId(),
                diary.getContent(),
                new DiaryImageGenerationCharacterPayload(
                        character.getId(),
                        character.getName(),
                        character.getCharacterVisionType().name(),
                        character.getBasePrompt()
                )
        );
    }
}
