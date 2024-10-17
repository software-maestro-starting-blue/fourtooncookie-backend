package com.startingblue.fourtooncookie.diary.exception;

import com.startingblue.fourtooncookie.diary.DiaryRepository;
import com.startingblue.fourtooncookie.diary.service.DiaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class DiaryExceptionTest {

    @Mock
    private DiaryRepository diaryRepository;

    @InjectMocks
    private DiaryService diaryService;

    private final UUID memberId = UUID.randomUUID();
    private final LocalDate diaryDate = LocalDate.of(2023, 9, 10);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @DisplayName("유저가 같은 날짜에 이미 일기를 쓴 경우 DiaryDuplicateException 발생")
    @Test
    void diaryDuplicateException() {
        when(diaryRepository.existsByMemberIdAndDiaryDate(memberId, diaryDate)).thenReturn(true);

        assertThrows(DiaryDuplicateException.class, () -> {
            diaryService.verifyUniqueDiary(memberId, diaryDate);
        });
    }
}
