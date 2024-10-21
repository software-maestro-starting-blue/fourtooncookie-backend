package com.startingblue.fourtooncookie.diary.listener;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startingblue.fourtooncookie.diary.DiaryService;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryStatus;
import com.startingblue.fourtooncookie.notification.NotificationService;
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
    private final NotificationService notificationService;

    @SqsListener(value = "${aws.sqs.fourtooncookie.image.response.sqs.fifo}")
    public CompletableFuture<Void> handleSQSMessage(String message) {
        return CompletableFuture.runAsync(() -> {
            try {
                DiaryImageResponseMessage response = parseMessage(message);
                log.info("received message: {}", response);
                if (!diaryService.existsById(response.diaryId())) {
                    log.info("Diary ID {} does not exist. Message will be ignored.", response.diaryId());
                    // 다이어리 ID가 없을 경우 처리를 하지 않음 (메시지 삭제)
                    return;
                }

                DiaryStatus currentDiaryStatus = diaryService.getById(response.diaryId()).getStatus();

                diaryService.processImageGenerationResponse(response);

                if (!DiaryStatus.IN_PROGRESS.equals(currentDiaryStatus)) {
                    return;
                }

                Diary diary = diaryService.getById(response.diaryId());
                if (!DiaryStatus.IN_PROGRESS.equals(diary.getStatus())) {
                    notificationService.sendNotificationToMember(diary.getMemberId(), diary);
                }
            } catch (JacksonException e) {
                log.error("Failed to parse message due to invalid format: {}", message, e);
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error occurred");
            }
        }).exceptionally(ex -> {
            log.error("Error handling SQS message", ex);
            return null;
        });
    }


    private DiaryImageResponseMessage parseMessage(String message) throws JacksonException {
        DiaryImageResponseMessage response = objectMapper.readValue(message, DiaryImageResponseMessage.class);
        return response;
    }

    public record DiaryImageResponseMessage(Long diaryId, int gridPosition, boolean isSuccess) {
    }
}
