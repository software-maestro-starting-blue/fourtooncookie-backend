package com.startingblue.fourtooncookie.aws.sqs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.startingblue.fourtooncookie.character.domain.Character;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@RequiredArgsConstructor
public class StableDiffusionSQSApplyService {

    private final SqsClient sqsClient;

    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.apply.url}")
    private String applyQueueUrl;

    public void sendMessage(Long diaryId, String prompt, Character character, Integer gridPosition) {
        String message = generateMessage(diaryId, prompt, character, gridPosition);

        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(applyQueueUrl)
                .messageBody(message)
                .messageGroupId("apply")
                .build();

        sqsClient.sendMessage(sendMessageRequest);
    }

    private String generateMessage(Long diaryId, String prompt, Character character, Integer gridPosition) {
        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("diaryId", diaryId);
            rootNode.put("prompt", prompt);
            rootNode.put("characterId", character.getId());
            rootNode.put("gridPosition", gridPosition);

            return objectMapper.writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json 구성 중 오류가 발생했습니다.", e);
        }
    }

}
