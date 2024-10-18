package com.startingblue.fourtooncookie.notification;

import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.notification.domain.NotificationToken;
import com.startingblue.fourtooncookie.notification.dto.NotificationTokenAssignRequest;
import com.startingblue.fourtooncookie.notification.dto.NotificationTokenUnassignRequest;
import com.startingblue.fourtooncookie.notification.exeption.NotificationSendException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final String EXPO_PUSH_SEND_API = "https://exp.host/--/api/v2/push/send";
    private final NotificationTokenRepository notificationTokenRepository;

    public void assignNotificationTokenToMember(final UUID memberId, final NotificationTokenAssignRequest notificationTokenAssignRequest) {
        notificationTokenRepository.findByToken(notificationTokenAssignRequest.notificationToken())
                .ifPresentOrElse(
                        token -> token.updateMember(memberId),
                        () -> notificationTokenRepository.save(new NotificationToken(notificationTokenAssignRequest.notificationToken(), memberId))
                );
    }

    @Transactional(readOnly = true)
    @Async
    public void sendNotificationToMember(final UUID memberId, final Diary diary) {
        final List<NotificationToken> notificationTokens = notificationTokenRepository.findByMemberId(memberId);

        final Map<String, Object> pushMessage = new HashMap<>();
        final List<String> list = notificationTokens.stream().map(NotificationToken::getToken).toList();
        pushMessage.put("to", list);
        pushMessage.put("title", "제목");
        pushMessage.put("body", "내용");

        final RestTemplate restTemplate = new RestTemplate();
        final String url = EXPO_PUSH_SEND_API;
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<Map<String, Object>> entity = new HttpEntity<>(pushMessage, headers);
        final ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            throw new NotificationSendException(exchange.getStatusCode() + exchange.getBody());
        }
    }

    @Transactional
    public void cleanupOldRecords() {
        notificationTokenRepository.deleteByModifiedDateTimeBefore(LocalDateTime.now().minusMinutes(1));
    }

    public void removeNotificationTokenFromMember(final UUID memberId, final NotificationTokenUnassignRequest notificationTokenAssignRequest) {
        notificationTokenRepository.findByMemberIdAndToken(memberId, notificationTokenAssignRequest.notificationToken())
                .ifPresent(notificationTokenRepository::delete);
    }
}
