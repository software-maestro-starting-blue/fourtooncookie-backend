package com.startingblue.fourtooncookie.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.startingblue.fourtooncookie.fcm.domain.FcmToken;
import com.startingblue.fourtooncookie.fcm.domain.FcmRepository;
import com.startingblue.fourtooncookie.fcm.dto.FcmRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    private final FcmRepository fcmRepository;

    public void sendFcmMessage(Long diaryId) {
        createFcmRequest();

        fcmRepository.findByDiaryId(diaryId).ifPresentOrElse(fcmToken -> {
            try {
                Message message = Message.builder()
                        .setToken(fcmToken.getFcmToken()) // 조회한 FCM 토큰 설정
                        .setNotification(Notification.builder()
                                .setTitle(diaryId + "님 알림도착했습니다!")
                                .setBody(diaryId + "일기가 완성됐습니다. 지금 당장 확인하세요!")
                                .build())
                        .build();
                String response = FirebaseMessaging.getInstance().send(message);
                log.info("FCM message sent successfully: {}", response);

            } catch (Exception e) {
                log.error("Error sending FCM message: {}", e.getMessage());
            }
        }, () -> log.warn("No FCM token found for Diary ID: " + diaryId));
    }

    private void createFcmRequest() {
    }
}
