package com.startingblue.fourtooncookie.aws.lambda.diaryImageGenerationPayload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startingblue.fourtooncookie.aws.lambda.LambdaInvoker;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.lambda.LambdaClient;

@Service
public class DiaryImageGenerationLambdaInvoker extends LambdaInvoker {

    private final ObjectMapper objectMapper;

    public DiaryImageGenerationLambdaInvoker(LambdaClient lambdaClient, ObjectMapper objectMapper) {
        super(lambdaClient);
        this.objectMapper = objectMapper;
    }

    @Override
    protected String serializePayload(Object payload) {
        try {
            DiaryImageGenerationLambdaPayload diaryPayload = (DiaryImageGenerationLambdaPayload) payload;
            return objectMapper.writeValueAsString(diaryPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("페이로드 직렬화 중 오류 발생", e);
        }
    }

    @Override
    protected String getFunctionName() {
        return "fourtooncookie-diaryimage-ai-apply-lambda";
    }

    public void invokeDiaryImageGenerationLambda(Diary diary, Character character) {
        DiaryImageGenerationLambdaPayload payload = new DiaryImageGenerationLambdaPayload(
                diary.getId(),
                diary.getContent(),
                new DiaryImageGenerationCharacterPayload(
                        character.getId(),
                        character.getName(),
                        character.getCharacterVisionType().name(),
                        character.getBasePrompt()
                )
        );
        invokeLambdaAsync(payload);
    }
}
