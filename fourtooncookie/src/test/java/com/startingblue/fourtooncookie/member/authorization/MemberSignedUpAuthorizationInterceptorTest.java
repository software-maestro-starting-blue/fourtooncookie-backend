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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberSignedUpAuthorizationInterceptorTest {
    @Mock
    private MemberService memberService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private MemberSignedUpAuthorizationInterceptor memberSignedUpAuthorizationInterceptor;

    @DisplayName("memberId가 empty인 경우 false 반환")
    @ParameterizedTest
    @ValueSource(strings = {""})
    void whenMemberIdEmptyThenFalse(String memberId) {
        //given & then
        when(request.getAttribute("memberId")).thenReturn(memberId);
        boolean result = memberSignedUpAuthorizationInterceptor.preHandle(request, response, new Object());

        //then
        assertFalse(result);
    }

    @DisplayName("memberId가 uuid가 아닌 경우 false 반환")
    @ParameterizedTest
    @ValueSource(strings = {"illegalId"})
    void whenMEmberIdIllegalThenFalse(String memberId) {
        //given & then
        when(request.getAttribute("memberId")).thenReturn(memberId);
        boolean result = memberSignedUpAuthorizationInterceptor.preHandle(request, response, new Object());

        //then
        assertFalse(result);
    }

    @DisplayName("member가 회원가입 가능한 경우 isAuthorized true 반환")
    @Test
    void whenAdminMemberIdThenTrue() {
        //given & then
        UUID uuid = UUID.randomUUID();
        when(memberService.verifyMemberSignUp(uuid)).thenReturn(true);
        boolean result = memberSignedUpAuthorizationInterceptor.isAuthorized(uuid);

        //then
        assertTrue(result);
    }

    @DisplayName("member가 회원가입 가능하지 않은 경우 isAuthorized false 반환")
    @Test
    void whenNotAdminMemberIdThenFalse() {
        //given & then
        UUID uuid = UUID.randomUUID();
        when(memberService.verifyMemberSignUp(uuid)).thenReturn(false);
        boolean result = memberSignedUpAuthorizationInterceptor.isAuthorized(uuid);

        //then
        assertFalse(result);
    }
}
