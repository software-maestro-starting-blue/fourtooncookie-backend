package com.startingblue.fourtooncookie.member.authorization;

import com.startingblue.fourtooncookie.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberAdminAuthorizationInterceptorTest {

    @Mock
    private MemberService memberService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private MemberAdminAuthorizationInterceptor memberAdminAuthorizationInterceptor;

    @DisplayName("memberId가 empty인 경우 false 반환")
    @ParameterizedTest
    @ValueSource(strings = {""})
    void whenMemberIdEmptyThenFalse(String memberId) {
        //given & then
        when(request.getAttribute("memberId")).thenReturn(memberId);
        when(request.getRequestURI()).thenReturn("");
        when(request.getMethod()).thenReturn(HttpMethod.GET.name());
        boolean result = memberAdminAuthorizationInterceptor.preHandle(request, response, new Object());

        //then
        assertFalse(result);
    }
    @DisplayName("memberId가 uuid가 아닌 경우 false 반환")
    @ParameterizedTest
    @ValueSource(strings = {"illegalId"})
    void whenMEmberIdIllegalThenFalse(String memberId) {
        //given & then
        when(request.getAttribute("memberId")).thenReturn(memberId);
        when(request.getRequestURI()).thenReturn("");
        when(request.getMethod()).thenReturn(HttpMethod.GET.name());
        boolean result = memberAdminAuthorizationInterceptor.preHandle(request, response, new Object());

        //then
        assertFalse(result);
    }

    @DisplayName("인가하는 경로인 경우 true 반환")
    @ParameterizedTest
    @ValueSource(strings = {"/character", "/artwork"})
    void whenPassableUriThenOk(String uri) {
        //given & then
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getMethod()).thenReturn(HttpMethod.GET.name());
        boolean result = memberAdminAuthorizationInterceptor.preHandle(request, response, new Object());

        //then
        assertTrue(result);
    }

    @DisplayName("인가하지 않는 경로인 경우 false 반환")
    @ParameterizedTest
    @ValueSource(strings = {"/diary"})
    void whenNotPassableUriThenNotOk(String uri) {
        //given & then
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getMethod()).thenReturn(HttpMethod.GET.name());
        boolean result = memberAdminAuthorizationInterceptor.preHandle(request, response, new Object());

        //then
        assertFalse(result);
    }

    @DisplayName("member가 admin인 경우 isAuthorized true 반환")
    @Test
    void whenAdminMemberIdThenTrue() {
        //given & then
        when(memberService.verifyMemberAdmin(any())).thenReturn(true);
        UUID randomUUID = UUID.randomUUID();
        boolean result = memberAdminAuthorizationInterceptor.isAuthorized(randomUUID);

        //then
        assertTrue(result);
    }

    @DisplayName("member가 admin이 아닌 경우 isAuthorized false 반환")
    @Test
    void whenNotAdminMemberIdThenFalse() {
        //given & then
        when(memberService.verifyMemberAdmin(any())).thenReturn(false);
        UUID randomUUID = UUID.randomUUID();
        boolean result = memberAdminAuthorizationInterceptor.isAuthorized(randomUUID);

        //then
        assertFalse(result);
    }
}
