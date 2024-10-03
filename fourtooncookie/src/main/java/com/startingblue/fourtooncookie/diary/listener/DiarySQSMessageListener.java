package com.startingblue.fourtooncookie.diary.listener;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryPaintingImageGenerationStatus;
import com.startingblue.fourtooncookie.diary.domain.DiaryRepository;
import com.startingblue.fourtooncookie.diary.domain.DiaryStatus;
import com.startingblue.fourtooncookie.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy.ON_SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiarySQSMessageListener {

    private final DiaryService diaryService;
    private final ObjectMapper objectMapper;
    private final DiaryRepository diaryRepository;

    private static final ThreadLocal<Boolean> isSqsRequest = ThreadLocal.withInitial(() -> false);

    public static void markAsSqsRequest() {
        isSqsRequest.set(true);
    }

    public static boolean isSqsRequest() {
        return isSqsRequest.get();
    }

    @SqsListener(value = "fourtooncookie_image_response_sqs.fifo", deletionPolicy = ON_SUCCESS)
    public void handleSQSMessage(String message) {
        try {
            markAsSqsRequest();
            DiaryImageResponseMessage response = objectMapper.readValue(message, DiaryImageResponseMessage.class);

            verifyJsonPayload(response);

            log.info("Received message: diaryId={}, gridPosition={}, isSuccess={}",
                    response.diaryId(),
                    response.gridPosition(),
                    response.isSuccess());

            Diary diary = diaryService.readById(response.diaryId());

            if (response.isSuccess()) {
                handleImageGenerationSuccess(diary, response.gridPosition());
            } else {
                handleImageGenerationFailure(diary, response.gridPosition());
            }
        }  catch (JacksonException e) {
            log.error("Failed to parse message due to invalid format: {}", message, e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while processing the message: {}", message, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void verifyJsonPayload(DiaryImageResponseMessage response) {
        if (response == null || response.diaryId() == null) {
            throw new IllegalArgumentException("Diary ID is missing in the message.");
        }
    }

    private void handleImageGenerationSuccess(Diary diary, int gridPosition) {
        updatePaintingImageGenerationStatus(diary, gridPosition, DiaryPaintingImageGenerationStatus.SUCCESS);

        if (diary.isImageGenerationComplete()) {
            diary.updateDiaryStatus(DiaryStatus.COMPLETED);
            diaryRepository.save(diary);
        }
    }

    private void handleImageGenerationFailure(Diary diary, int gridPosition) {
        updatePaintingImageGenerationStatus(diary, gridPosition, DiaryPaintingImageGenerationStatus.FAILURE);
    }

    private void updatePaintingImageGenerationStatus(Diary diary, int gridPosition, DiaryPaintingImageGenerationStatus status) {
        diary.updatePaintingImageGenerationStatus(gridPosition, status);
        diaryRepository.save(diary);
    }

    public record DiaryImageResponseMessage(Long diaryId, int gridPosition, boolean isSuccess) {
    }
}
