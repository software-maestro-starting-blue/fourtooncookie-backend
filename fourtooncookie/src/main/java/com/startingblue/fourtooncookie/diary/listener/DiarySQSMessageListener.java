package com.startingblue.fourtooncookie.diary.listener;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startingblue.fourtooncookie.diary.service.DiaryService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiarySQSMessageListener {

    private final DiaryService diaryService;
    private final ObjectMapper objectMapper;

    @SqsListener(value = "${aws.sqs.fourtooncookie.image.response.sqs.fifo}", factory = "defaultSqsListenerContainerFactory")
    public void handleSQSMessage(String message) {
        try {
            DiaryImageResponseMessage response = parseMessage(message);
            diaryService.processImageGenerationResponse(response);
        } catch (JacksonException e) {
            log.error("Failed to parse message due to invalid format: {}", message, e);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred", e);
        }
    }

    private DiaryImageResponseMessage parseMessage(String message) throws JacksonException {
        return Optional.ofNullable(objectMapper.readValue(message, DiaryImageResponseMessage.class))
                .filter(response -> response.diaryId() != null)
                .orElseThrow(() -> new IllegalArgumentException("Diary ID is missing in the message."));
    }

    public record DiaryImageResponseMessage(Long diaryId, int gridPosition, boolean isSuccess) {
    }
}
