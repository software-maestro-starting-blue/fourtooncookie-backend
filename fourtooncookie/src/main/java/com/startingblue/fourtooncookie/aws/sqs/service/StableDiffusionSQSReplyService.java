package com.startingblue.fourtooncookie.aws.sqs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startingblue.fourtooncookie.vision.reply.dto.VisionReplyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@Service
@RequiredArgsConstructor
public class StableDiffusionSQSReplyService {

    private final SqsClient sqsClient;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${aws.sqs.reply.url}")
    private String replyQueueUrl;

    @Value("${aws.sqs.reply.max-message-count}")
    private Integer maxMessageCount;

    @Scheduled(fixedDelay = 1L)
    public void handleMessages() {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(replyQueueUrl)
                .maxNumberOfMessages(maxMessageCount)
                .build();

        ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);

        receiveMessageResponse.messages()
                .forEach(message -> {
                    VisionReplyEvent visionReplyEvent = convertMessageToVisionReplyEvent(message.body());
                    applicationEventPublisher.publishEvent(visionReplyEvent);
                    deleteMessage(message);
                });
    }

    private void deleteMessage(Message message) {
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(replyQueueUrl)
                .receiptHandle(message.receiptHandle())
                .build();

        sqsClient.deleteMessage(deleteMessageRequest);
    }

    private VisionReplyEvent convertMessageToVisionReplyEvent(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(message);

            Long diaryId = rootNode.get("diaryId").asLong();
            byte[] image = rootNode.get("image").binaryValue();
            Integer gridPosition = rootNode.get("gridPosition").asInt();

            return new VisionReplyEvent(diaryId, image, gridPosition);
        } catch (Exception e) {
            throw new RuntimeException("메시지를 VisionReplyEvent로 변환하는 중 오류가 발생했습니다.", e);
        }
    }

}
