package com.startingblue.fourtooncookie.diary.authorization;

import com.startingblue.fourtooncookie.diary.DiaryService;
import com.startingblue.fourtooncookie.web.interceptor.DiaryOwnerAuthorizationInterceptor;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class DiaryOwnerAuthorizationInterceptorTest {

    @Mock
    private DiaryService diaryService;

    @InjectMocks
    private DiaryOwnerAuthorizationInterceptor diaryOwnerAuthorizationInterceptor;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("유효한 memberId와 diaryId로 권한이 있을 때 - 성공")
    void preHandleSuccessAuthorized() {
        // Given
        String validMemberId = UUID.randomUUID().toString();
        long validDiaryId = 1L;

        request.setRequestURI("/diary/" + validDiaryId);
        request.setAttribute("memberId", validMemberId);

        when(diaryService.verifyDiaryOwner(UUID.fromString(validMemberId), validDiaryId)).thenReturn(true);

        // When
        boolean result = diaryOwnerAuthorizationInterceptor.preHandle(request, response, new Object());

        // Then
        assertTrue(result);
        verify(diaryService, times(1)).verifyDiaryOwner(UUID.fromString(validMemberId), validDiaryId);
    }

    @Test
    @DisplayName("유효하지 않은 memberId로 권한 실패 - memberId 누락")
    void preHandleFailureMissingMemberId() {
        // Given
        request.setRequestURI("/diary/1");
        request.setAttribute("memberId", null);

        // When
        boolean result = diaryOwnerAuthorizationInterceptor.preHandle(request, response, new Object());

        // Then
        assertFalse(result);
        assertTrue(response.getStatus() == HttpServletResponse.SC_FORBIDDEN);
        verify(diaryService, never()).verifyDiaryOwner(any(UUID.class), anyLong());
    }

    @Test
    @DisplayName("유효하지 않은 diaryId로 권한 실패 - diaryId 누락")
    void preHandleFailureMissingDiaryId() {
        // Given
        String validMemberId = UUID.randomUUID().toString();
        request.setRequestURI("/diary/");
        request.setAttribute("memberId", validMemberId);

        // When
        boolean result = diaryOwnerAuthorizationInterceptor.preHandle(request, response, new Object());

        // Then
        assertFalse(result);
        assertTrue(response.getStatus() == HttpServletResponse.SC_FORBIDDEN);
        verify(diaryService, never()).verifyDiaryOwner(any(UUID.class), anyLong());
    }

    @Test
    @DisplayName("유효한 memberId와 diaryId로 권한 실패 - 권한 없음")
    void preHandleFailureNotAuthorized() {
        // Given
        String validMemberId = UUID.randomUUID().toString();
        long validDiaryId = 1L;

        request.setRequestURI("/diary/" + validDiaryId);
        request.setAttribute("memberId", validMemberId);

        when(diaryService.verifyDiaryOwner(UUID.fromString(validMemberId), validDiaryId)).thenReturn(false);

        // When
        boolean result = diaryOwnerAuthorizationInterceptor.preHandle(request, response, new Object());

        // Then
        assertFalse(result);
        assertTrue(response.getStatus() == HttpServletResponse.SC_FORBIDDEN);
        verify(diaryService, times(1)).verifyDiaryOwner(UUID.fromString(validMemberId), validDiaryId);
    }

    @ParameterizedTest
    @CsvSource({
            "null",
            "'',",
            "' '",
            "invalid-uuid"
    })
    @DisplayName("유효하지 않은 memberId 형식 - 실패")
    void preHandleFailureInvalidMemberId(String memberId) {
        // Given
        request.setRequestURI("/diary/1");
        request.setAttribute("memberId", memberId);

        // When
        boolean result = diaryOwnerAuthorizationInterceptor.preHandle(request, response, new Object());

        // Then
        assertFalse(result);
        assertTrue(response.getStatus() == HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden 응답
        verify(diaryService, never()).verifyDiaryOwner(any(UUID.class), anyLong()); // diaryService는 호출되지 않아야 함
    }
}
