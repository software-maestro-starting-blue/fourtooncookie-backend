package com.startingblue.fourtooncookie.notification;

import com.startingblue.fourtooncookie.notification.dto.NotificationTokenAssignRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationTokenRepository notificationTokenRepository;

    @DisplayName("토큰하나를 한 멤버에 할당")
    @Test
    void assignTokenToMember() {
        //given
        final UUID memberId = UUID.fromString("12345678-1234-1234-1234-123456789abc");
        final String token = "token";
        final NotificationTokenAssignRequest notificationTokenAssignRequest = new NotificationTokenAssignRequest(token);

        //when
        notificationService.assignNotificationTokenToMember(Locale.KOREAN, memberId, notificationTokenAssignRequest);

        //then
        final String actual = notificationTokenRepository.findByMemberId(memberId).get(0).getToken();
        assertEquals(token, actual);
    }

    @DisplayName("여러 토큰을 한 멤버에 할당")
    @Test
    void assignTokensToMember() {
        //given
        final UUID memberId = UUID.fromString("12345678-1234-1234-1234-123456789abc");

        final String token = "token";
        final NotificationTokenAssignRequest notificationTokenAssignRequest = new NotificationTokenAssignRequest(token);

        final String token2 = "token2";
        final NotificationTokenAssignRequest notificationTokenAssignRequest2 = new NotificationTokenAssignRequest(token2);

        //when
        notificationService.assignNotificationTokenToMember(Locale.KOREAN, memberId, notificationTokenAssignRequest);
        notificationService.assignNotificationTokenToMember(Locale.KOREAN, memberId, notificationTokenAssignRequest2);

        //then
        assertEquals(2, notificationTokenRepository.findByMemberId(memberId).size());
    }

    @DisplayName("중복된 토큰을 새 멤버에 할당")
    @Test
    void assignDuplicateTokenToMember() {
        //given
        final UUID beforeMemberId = UUID.fromString("12345678-1234-1234-1234-123456789abc");
        final UUID afterMemberId = UUID.fromString("87654321-4321-4321-4321-cba987654321");

        final String token = "token";
        final NotificationTokenAssignRequest notificationTokenAssignRequest = new NotificationTokenAssignRequest(token);

        //when
        notificationService.assignNotificationTokenToMember(Locale.KOREAN, beforeMemberId, notificationTokenAssignRequest);
        notificationService.assignNotificationTokenToMember(Locale.KOREAN, afterMemberId, notificationTokenAssignRequest);

        //then
        assertEquals(0, notificationTokenRepository.findByMemberId(beforeMemberId).size());
        assertEquals(1, notificationTokenRepository.findByMemberId(afterMemberId).size());
    }
}
