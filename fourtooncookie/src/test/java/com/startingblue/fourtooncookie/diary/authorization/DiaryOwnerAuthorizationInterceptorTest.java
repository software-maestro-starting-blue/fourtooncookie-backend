package com.startingblue.fourtooncookie.diary.authorization;

import com.startingblue.fourtooncookie.diary.service.DiaryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class DiaryOwnerAuthorizationInterceptorTest {

    @Mock
    private DiaryService diaryService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private DiaryOwnerAuthorizationInterceptor diaryOwnerAuthorizationInterceptor;

    private final UUID memberId = UUID.randomUUID();
    private final Long diaryId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Authorization succeeds when member is the diary owner")
    @Test
    void testPreHandle_Authorized() throws Exception {
        // Simulate the request having the correct memberId and diaryId
        when(request.getAttribute("memberId")).thenReturn(memberId.toString());
        when(request.getRequestURI()).thenReturn("/diary/" + diaryId);

        // Simulate that the member is authorized
        when(diaryService.verifyDiaryOwner(memberId, diaryId)).thenReturn(true);

        // Call the interceptor
        boolean result = diaryOwnerAuthorizationInterceptor.preHandle(request, response, new Object());

        // Verify the outcome
        assertTrue(result);
        verify(response, never()).setStatus(SC_FORBIDDEN);
    }

    @DisplayName("Authorization fails when member is not the diary owner")
    @Test
    void testPreHandle_Unauthorized() throws Exception {
        // Simulate the request having the correct memberId and diaryId
        when(request.getAttribute("memberId")).thenReturn(memberId.toString());
        when(request.getRequestURI()).thenReturn("/diary/" + diaryId);

        // Simulate that the member is not authorized
        when(diaryService.verifyDiaryOwner(memberId, diaryId)).thenReturn(false);

        // Call the interceptor
        boolean result = diaryOwnerAuthorizationInterceptor.preHandle(request, response, new Object());

        // Verify the outcome
        assertFalse(result);
        verify(response).setStatus(SC_FORBIDDEN);  // Expecting the response to set 403 status
    }

    @DisplayName("Fails when memberId is missing or invalid")
    @Test
    void testPreHandle_InvalidMemberId() throws Exception {
        // Simulate the request having a missing or invalid memberId
        when(request.getAttribute("memberId")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/diary/" + diaryId);

        // Call the interceptor
        boolean result = diaryOwnerAuthorizationInterceptor.preHandle(request, response, new Object());

        // Verify the outcome
        assertFalse(result);
        verify(response).setStatus(SC_FORBIDDEN);
    }

    @DisplayName("Fails when diaryId is missing or invalid")
    @Test
    void testPreHandle_InvalidDiaryId() throws Exception {
        // Simulate the request having a valid memberId but missing/invalid diaryId
        when(request.getAttribute("memberId")).thenReturn(memberId.toString());
        when(request.getRequestURI()).thenReturn("/diary/");

        // Call the interceptor
        boolean result = diaryOwnerAuthorizationInterceptor.preHandle(request, response, new Object());

        // Verify the outcome
        assertFalse(result);
        verify(response).setStatus(SC_FORBIDDEN);
    }

    @DisplayName("Fails when memberId is invalid UUID")
    @Test
    void testPreHandle_InvalidUUID() throws Exception {
        // Simulate the request having an invalid memberId format
        when(request.getAttribute("memberId")).thenReturn("invalid-uuid");
        when(request.getRequestURI()).thenReturn("/diary/" + diaryId);

        // Call the interceptor
        boolean result = diaryOwnerAuthorizationInterceptor.preHandle(request, response, new Object());

        // Verify the outcome
        assertFalse(result);
        verify(response).setStatus(SC_FORBIDDEN);  // Expecting the response to set 403 status
    }
}
