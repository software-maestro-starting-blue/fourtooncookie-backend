package com.startingblue.fourtooncookie.aws.lambda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startingblue.fourtooncookie.aws.lambda.diaryImageGenerationPayload.DiaryImageGenerationCharacterPayload;
import com.startingblue.fourtooncookie.aws.lambda.diaryImageGenerationPayload.DiaryImageGenerationLambdaPayload;
import com.startingblue.fourtooncookie.character.domain.Character;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.exception.DiaryLambdaInvocationException;
import com.startingblue.fourtooncookie.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.core.SdkBytes;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class LambdaInvoker {

    private static final String IMAGE_GENERATE_LAMBDA_FUNCTION_NAME = "fourtooncookie-diaryimage-ai-apply-lambda";

    private final LambdaClient lambdaClient;
    private final ObjectMapper objectMapper;

    public void invokeImageGenerateLambdaAsync(Diary diary, Character character) {
        String payload = buildLambdaPayload(diary, character);
        try {
            invokeLambda(IMAGE_GENERATE_LAMBDA_FUNCTION_NAME, payload);
            log.info("Lambda 호출 성공: 일기 ID - {}", diary.getId());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lambda invoke error", e);
        }
    }

    public void invokeLambda(String functionName, String payload) {
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(functionName)
                .payload(SdkBytes.fromUtf8String(payload))
                .invocationType(InvocationType.REQUEST_RESPONSE)
                .build();

        var response = lambdaClient.invoke(invokeRequest);
        if (response.payload().asUtf8String().equals("false")) {
            throw new RuntimeException("Lambda invocation failed");
        }
    }

    private String buildLambdaPayload(Diary diary, Character character) {
        DiaryImageGenerationCharacterPayload characterResponse = new DiaryImageGenerationCharacterPayload(
                character.getId(),
                character.getName(),
                character.getCharacterVisionType().name(),
                character.getBasePrompt()
        );

        DiaryImageGenerationLambdaPayload payload = new DiaryImageGenerationLambdaPayload(
                diary.getId(),
                diary.getContent(),
                characterResponse
        );
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new DiaryLambdaInvocationException("Lambda payload 직렬화 중 오류가 발생했습니다.", e);
        }
    }
}
