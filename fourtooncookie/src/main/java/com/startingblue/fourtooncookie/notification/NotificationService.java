package com.startingblue.fourtooncookie.notification;

import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.notification.domain.NotificationToken;
import com.startingblue.fourtooncookie.notification.dto.NotificationTokenAssignRequest;
import com.startingblue.fourtooncookie.notification.dto.NotificationTokenUnassignRequest;
import com.startingblue.fourtooncookie.notification.exeption.NotificationSendException;
import com.startingblue.fourtooncookie.notification.service.NotificationMessageSourceService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final String EXPO_PUSH_SEND_API = "https://exp.host/--/api/v2/push/send";
    private static final String EXPO_NOTIFICATION_TO = "to";
    private static final String EXPO_NOTIFICATION_TITLE = "title";
    private static final String EXPO_NOTIFICATION_BODY = "body";
    private final NotificationTokenRepository notificationTokenRepository;
    private final NotificationMessageSourceService notificationMessageSourceService;

    public void assignNotificationTokenToMember(final UUID memberId, final Locale locale, final NotificationTokenAssignRequest notificationTokenAssignRequest) {
        notificationTokenRepository.findByToken(notificationTokenAssignRequest.notificationToken())
                .ifPresentOrElse(
                        token -> token.updateMember(memberId),
                        () -> notificationTokenRepository.save(new NotificationToken(notificationTokenAssignRequest.notificationToken(), memberId, locale))
                );
    }

    @Transactional(readOnly = true)
    @Async
    public void sendNotificationToMember(final UUID memberId, final Diary diary) {
        final List<NotificationToken> notificationTokens = notificationTokenRepository.findByMemberId(memberId);

        final Map<Locale, List<String>> pushNotification = new HashMap<>();
        notificationTokens.forEach(token -> {
            if (!pushNotification.containsKey(token.getLocale())) {
                pushNotification.put(token.getLocale(), new ArrayList<>());
            }
            pushNotification.get(token.getLocale()).add(token.getToken());
        });

        pushNotification.keySet().forEach(locale -> {
            String title = notificationMessageSourceService.getMessage("notification.title" + diary.getStatus().toString().toLowerCase());
            String content = notificationMessageSourceService.getMessage("notification.content" + diary.getStatus().toString().toLowerCase());
            sendMessageByPushMessage(pushNotification.get(locale), title, content);
        });
    }

    private void sendMessageByPushMessage(final List<String> to, final String title, final String body) {
        final Map<String, Object> pushMessage = new HashMap<>();
        pushMessage.put(EXPO_NOTIFICATION_TO, to);
        pushMessage.put(EXPO_NOTIFICATION_TITLE, title);
        pushMessage.put(EXPO_NOTIFICATION_BODY, body);

        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<Map<String, Object>> entity = new HttpEntity<>(pushMessage, headers);
        final ResponseEntity<String> exchange = restTemplate.exchange(EXPO_PUSH_SEND_API, HttpMethod.POST, entity, String.class);

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

    public void removeAllNotificationTokenFromMember(final UUID memberId) {
        notificationTokenRepository.deleteByMemberId(memberId);
    }
}
