package com.startingblue.fourtooncookie.diary.listener;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startingblue.fourtooncookie.diary.service.DiaryService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiarySQSMessageListener {

    private final DiaryService diaryService;
    private final ObjectMapper objectMapper;

    @SqsListener(value = "${aws.sqs.fourtooncookie.image.response.sqs.fifo}")
    public CompletableFuture<Void> handleSQSMessage(String message) {
        return CompletableFuture.runAsync(() -> {
            try {
                DiaryImageResponseMessage response = parseMessage(message);
                log.info("received message: {}", response);
                diaryService.processImageGenerationResponse(response);
            } catch (JacksonException e) {
                log.error("Failed to parse message due to invalid format: {}", message, e);
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error occurred", e);
            }
        }).exceptionally(ex -> {
            log.error("Error handling SQS message", ex);
            return null;
        });
    }


    private DiaryImageResponseMessage parseMessage(String message) throws JacksonException {
        DiaryImageResponseMessage response = objectMapper.readValue(message, DiaryImageResponseMessage.class);
        if (response.diaryId() == null) {
            throw new IllegalArgumentException("Diary ID is missing in the message.");
        }
        return response;
    }

    public record DiaryImageResponseMessage(Long diaryId, int gridPosition, boolean isSuccess) {
    }
}
