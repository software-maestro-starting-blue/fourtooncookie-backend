package com.startingblue.fourtooncookie.diary.listener;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startingblue.fourtooncookie.diary.service.DiaryService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            log.error("Invalid payload in message: {}", message, e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while processing the message: {}", message, e);
            throw new RuntimeException(e);
        }
    }

    private DiaryImageResponseMessage parseMessage(String message) throws JacksonException {
        DiaryImageResponseMessage response = objectMapper.readValue(message, DiaryImageResponseMessage.class);
        verifyJsonPayload(response);
        return response;
    }

    private static void verifyJsonPayload(DiaryImageResponseMessage response) {
        if (response == null || response.diaryId() == null) {
            throw new IllegalArgumentException("Diary ID is missing in the message.");
        }
    }

    public record DiaryImageResponseMessage(Long diaryId, int gridPosition, boolean isSuccess) {
    }
}
