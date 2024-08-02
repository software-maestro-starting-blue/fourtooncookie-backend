package com.startingblue.fourtooncookie.member.authorization;

import com.startingblue.fourtooncookie.jwt.JwtExtractor;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MemberAuthorizationInterceptorTest {

    @InjectMocks
    private MemberAuthorizationInterceptor interceptor;

    @Mock
    private JwtExtractor jwtExtractor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Claims claims;

    private final String validToken = "valid.token";
    private final UUID memberId = UUID.randomUUID();
    private final String memberUUIDString = memberId.toString();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Map.of("memberId", memberUUIDString));
        when(jwtExtractor.resolveToken(request)).thenReturn(validToken);
        when(jwtExtractor.parseToken(validToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(memberUUIDString);
    }

    @Test
    @DisplayName("모든 조건이 충족될 때 인증 성공")
    void preHandle_Authorized_Success() throws Exception {
        assertTrue(interceptor.preHandle(request, response, null));
        verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("토큰 미제공 시 인증 실패")
    void preHandle_TokenNotFound_Failure() {
        when(jwtExtractor.resolveToken(any(HttpServletRequest.class))).thenReturn("");
        assertFalse(interceptor.preHandle(request, response, null));
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("토큰 내 멤버 ID가 유효하지 않을 경우 인증 실패")
    void preHandle_InvalidMemberIdInToken_Failure() {
        when(claims.getSubject()).thenThrow(new IllegalArgumentException("Invalid UUID"));
        assertFalse(interceptor.preHandle(request, response, null));
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("경로 변수에서 멤버 ID 누락 시 인증 실패")
    void preHandle_PathVariableNotFound_Failure() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Map.of());
        assertFalse(interceptor.preHandle(request, response, null));
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("경로 변수의 멤버 ID가 유효한 UUID가 아닐 경우 인증 실패")
    void preHandle_InvalidPathVariableUUID_Failure() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(Map.of("memberId", "not-a-uuid"));
        assertFalse(interceptor.preHandle(request, response, null));
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
