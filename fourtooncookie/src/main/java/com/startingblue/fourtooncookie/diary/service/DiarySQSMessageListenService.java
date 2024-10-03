package com.startingblue.fourtooncookie.diary.service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryPaintingImageGenerationStatus;
import com.startingblue.fourtooncookie.diary.domain.DiaryRepository;
import com.startingblue.fourtooncookie.diary.domain.DiaryStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy.ON_SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiarySQSMessageListenService {

    private final DiaryService diaryService;
    private final ObjectMapper objectMapper;
    private final DiaryRepository diaryRepository;

    @SqsListener(value = "${aws.sqs.fourtooncookie.image.response.sqs.fifo}", deletionPolicy = ON_SUCCESS)
    public void handleSQSMessage(String message) {
        try {
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
