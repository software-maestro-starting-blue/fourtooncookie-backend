package com.startingblue.fourtooncookie.config.authentication;

import com.startingblue.fourtooncookie.web.exception.AuthenticationException;
import com.startingblue.fourtooncookie.web.filter.AuthenticationFilter;
import com.startingblue.fourtooncookie.web.filter.jwt.JwtExtractor;
import com.startingblue.fourtooncookie.member.service.MemberService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.*;

class AuthenticationFilterTest {

    @Mock
    private JwtExtractor jwtExtractor;

    @Mock
    private MemberService memberService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthenticationFilter authenticationFilter;

    private UUID memberId;
    private Claims claims;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        memberId = UUID.randomUUID();
        claims = mock(Claims.class); // Mocking JWT claims
    }

    @Test
    @DisplayName("인증 성공 - 회원 존재")
    void doFilterSuccessValidTokenAndMemberExists() throws IOException, ServletException {
        // Given
        when(request.getRequestURI()).thenReturn("/member");
        when(request.getMethod()).thenReturn("GET");

        when(jwtExtractor.resolveToken(request)).thenReturn("validToken");
        when(jwtExtractor.parseToken("validToken")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(memberId.toString());

        when(memberService.verifyMemberExists(memberId)).thenReturn(true);

        // When
        authenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response); // 다음 체인으로 넘어가야함
    }

    @Test
    @DisplayName("인증 실패 - 유효하지 않은 토큰")
    void doFilterFailureInvalidToken() throws IOException, ServletException {
        // Given
        when(request.getRequestURI()).thenReturn("/member");
        when(request.getMethod()).thenReturn("GET");

        when(jwtExtractor.resolveToken(request)).thenReturn("invalidToken");
        when(jwtExtractor.parseToken("invalidToken")).thenThrow(new AuthenticationException("Invalid token"));

        authenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed: Invalid token");
        verify(filterChain, never()).doFilter(request, response); // 에러 발생 -> 체인으로 넘어가면 안됨
    }

    @Test
    @DisplayName("인증 실패 - 회원 존재하지 않음")
    void doFilterFailureMemberNotExists() throws IOException, ServletException {
        // Given
        when(request.getRequestURI()).thenReturn("/member");
        when(request.getMethod()).thenReturn("GET");

        when(jwtExtractor.resolveToken(request)).thenReturn("validToken");
        when(jwtExtractor.parseToken("validToken")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(memberId.toString());

        when(memberService.verifyMemberExists(memberId)).thenReturn(false);

        // When
        authenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND, String.format("Member with id %s not found", memberId));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("회원가입 요청 처리 - 성공")
    void doFilterSuccessSignupRequest() throws IOException, ServletException {
        // Given
        when(request.getRequestURI()).thenReturn("/member");
        when(request.getMethod()).thenReturn("POST");

        when(jwtExtractor.resolveToken(request)).thenReturn("validToken");
        when(jwtExtractor.parseToken("validToken")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(memberId.toString());

        // When
        authenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(request, times(1)).setAttribute("memberId", memberId); // memberId가 설정되어야 함
        verify(filterChain, times(1)).doFilter(request, response);    // 필터 체인이 계속 이어져야 함
        verify(response, never()).sendError(anyInt(), anyString());  // 에러는 발생하지 않아야 함
    }

    @Test
    @DisplayName("회원 존재 요청 처리 - 성공")
    void doFilterSuccessMemberExists() throws IOException, ServletException {
        // Given
        when(request.getRequestURI()).thenReturn("/member/profile");
        when(request.getMethod()).thenReturn("GET");

        // Mock 토큰 추출 및 파싱
        when(jwtExtractor.resolveToken(request)).thenReturn("validToken");
        when(jwtExtractor.parseToken("validToken")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(memberId.toString());

        // Mock 회원 존재 여부
        when(memberService.verifyMemberExists(memberId)).thenReturn(true);

        // When
        authenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(request, times(1)).setAttribute("memberId", memberId); // memberId가 설정되어야 함
        verify(filterChain, times(1)).doFilter(request, response);    // 필터 체인이 계속 이어져야 함
        verify(response, never()).sendError(anyInt(), anyString());  // 에러는 발생하지 않아야 함
    }

    @Test
    @DisplayName("인증 우회 - [GET] /health URI")
    void doFilterBypassAuthenticationHealth() throws IOException, ServletException {
        // Given
        when(request.getRequestURI()).thenReturn("/health");
        when(request.getMethod()).thenReturn("GET");

        // When
        authenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString()); // 에러가 발생하지 않아야 함
    }

    @Test
    @DisplayName("인증 우회 - [GET] /artwork URI")
    void doFilterBypassAuthenticationGetArtwork() throws IOException, ServletException {
        // Given
        when(request.getRequestURI()).thenReturn("/artwork");
        when(request.getMethod()).thenReturn("GET");

        // When
        authenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }
    @Test
    @DisplayName("인증 우회 - [GET] /character URI")
    void doFilterBypassAuthenticationGetCharacter() throws IOException, ServletException {
        // Given
        when(request.getRequestURI()).thenReturn("/character");
        when(request.getMethod()).thenReturn("GET");

        // When
        authenticationFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

}
