package com.startingblue.fourtooncookie.aws.sqs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startingblue.fourtooncookie.aws.sqs.exception.SQSMessageConversionException;
import com.startingblue.fourtooncookie.aws.sqs.exception.SQSMessageDeletionException;
import com.startingblue.fourtooncookie.aws.sqs.exception.SQSMessageProcessingException;
import com.startingblue.fourtooncookie.vision.reply.dto.VisionReplyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StableDiffusionSQSReplyService {

    private static final int FIXED_DELAY = 30 * 60 * 1000;
    private static final String CONVERSION_ERROR_MESSAGE = "메시지를 VisionReplyEvent로 변환하는 중 오류가 발생했습니다.";
    private static final String DELETE_MESSAGE_ERROR_MESSAGE = "메시지를 삭제하는 중 오류가 발생했습니다.";

    private final SqsClient sqsClient;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.reply.url}")
    private String replyQueueUrl;

    @Value("${aws.sqs.reply.max-message-count}")
    private Integer maxMessageCount;

    @Scheduled(fixedDelay = FIXED_DELAY)
    public void handleMessages() {
        List<Message> messages = getMessagesFromSQS();
        processMessages(messages);
    }

    private List<Message> getMessagesFromSQS() {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(replyQueueUrl)
                .maxNumberOfMessages(maxMessageCount)
                .build();

        ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);
        return receiveMessageResponse.messages();
    }

    private void processMessages(List<Message> messages) {
        for (Message message : messages) {
            try {
                VisionReplyEvent visionReplyEvent = convertMessageToVisionReplyEvent(message.body());
                applicationEventPublisher.publishEvent(visionReplyEvent);
                deleteMessage(message);
            } catch (Exception e) {
                throw new SQSMessageProcessingException("Unexpected error occurred while sending message to SQS", e);
            }
        }
    }

    private VisionReplyEvent convertMessageToVisionReplyEvent(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);

            Long diaryId = rootNode.get("diaryId").asLong();
            byte[] image = rootNode.get("image").binaryValue();
            Integer gridPosition = rootNode.get("gridPosition").asInt();

            return new VisionReplyEvent(diaryId, image, gridPosition);
        } catch (Exception e) {
            throw new SQSMessageConversionException(CONVERSION_ERROR_MESSAGE, e);
        }
    }

    private void deleteMessage(Message message) {
        try {
            DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                    .queueUrl(replyQueueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build();

            sqsClient.deleteMessage(deleteMessageRequest);
        } catch (Exception e) {
            throw new SQSMessageDeletionException(DELETE_MESSAGE_ERROR_MESSAGE, e);
        }
    }
}
