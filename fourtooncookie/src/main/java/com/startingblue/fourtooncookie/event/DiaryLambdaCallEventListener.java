package com.startingblue.fourtooncookie.event;

import com.startingblue.fourtooncookie.aws.lambda.LambdaInvoker;
import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.diary.domain.DiaryStatus;
import com.startingblue.fourtooncookie.diary.service.DiaryService;
import com.startingblue.fourtooncookie.event.domain.DiaryLambdaCallEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class DiaryLambdaCallEventListener {

    private final LambdaInvoker lambdaInvoker;
    private final DiaryService diaryService;

    @Async
    @EventListener
    @Transactional
    public void handleDiaryLambdaCallEvent(DiaryLambdaCallEvent event) {
        try {
            lambdaInvoker.invokeImageGenerateLambdaAsync(event.diary(), event.character());
        } catch (RuntimeException e) {
            handleLambdaInvocationFailure(event.diary(), e);
        }
    }

    private void handleLambdaInvocationFailure(Diary diary, Throwable ex) {
        log.error("Lambda 호출 중 오류 발생, 저장된 일기 삭제: {}", ex.getMessage());
        Diary foundDiary = diaryService.readById(diary.getId()); // todo 트랜잭션 변경으로 영속성 컨텍스트가 변경 될 것 같아 우선 다시 가져옴. 테스트 필요
        foundDiary.update("일기 생성 중 오류가 발생했습니다. 일기를 삭제 후 다시 생성해 주세요.",
                diary.getCharacter(),
                DiaryStatus.FAILED);
    }
}
