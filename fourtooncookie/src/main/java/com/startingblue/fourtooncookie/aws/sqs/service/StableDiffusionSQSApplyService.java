package com.startingblue.fourtooncookie.aws.sqs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.startingblue.fourtooncookie.aws.sqs.exception.SQSMessageProcessingException;
import com.startingblue.fourtooncookie.character.domain.Character;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

@Slf4j
@Service
@RequiredArgsConstructor
public class StableDiffusionSQSApplyService {

    private static final String MESSAGE_GROUP_ID = "apply";
    private static final String JSON_PROCESSING_ERROR = "Json 구성 중 오류가 발생했습니다.";
    private static final String SQS_SEND_ERROR = "SQS로 메시지를 전송 중 오류가 발생했습니다.";

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.apply.url}")
    private String applyQueueUrl;

    public void sendMessage(Long diaryId, String prompt, Character character, Integer gridPosition) {
        try {
            String message = generateMessage(diaryId, prompt, character, gridPosition);
            sendToSqs(message);
        } catch (JsonProcessingException e) {
            throw new SQSMessageProcessingException(JSON_PROCESSING_ERROR, e);
        } catch (SqsException e) {
            throw new SQSMessageProcessingException(SQS_SEND_ERROR, e);
        } catch (Exception e) {
            throw new SQSMessageProcessingException("Unexpected error occurred while sending message to SQS", e);
        }
    }

    private String generateMessage(Long diaryId, String prompt, Character character, Integer gridPosition) throws JsonProcessingException {
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("diaryId", diaryId);
        rootNode.put("prompt", prompt);
        rootNode.put("characterId", character.getId());
        rootNode.put("gridPosition", gridPosition);
        return objectMapper.writeValueAsString(rootNode);
    }

    private void sendToSqs(String message) {
        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(applyQueueUrl)
                .messageBody(message)
                .messageGroupId(MESSAGE_GROUP_ID)
                .build();
        sqsClient.sendMessage(sendMessageRequest);
    }

}
