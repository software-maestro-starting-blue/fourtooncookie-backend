package com.startingblue.fourtooncookie.diary.listener;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryPaintingImageGenerationStatus;
import com.startingblue.fourtooncookie.diary.domain.DiaryRepository;
import com.startingblue.fourtooncookie.diary.domain.DiaryStatus;
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
    private final DiaryRepository diaryRepository;

    @SqsListener(value = "${aws.sqs.fourtooncookie.image.response.sqs.fifo}", factory = "defaultSqsListenerContainerFactory")
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
