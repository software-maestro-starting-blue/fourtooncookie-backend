package com.startingblue.fourtooncookie.notification;

import com.startingblue.fourtooncookie.notification.domain.NotificationToken;
import com.startingblue.fourtooncookie.notification.dto.NotificationTokenAssignRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class ExpoNotificationService implements NotificationService {

    private final NotificationTokenRepository notificationTokenRepository;

    @Override
    public void assignNotificationTokenToMember(final UUID memberId, final NotificationTokenAssignRequest notificationTokenAssignRequest) {
        notificationTokenRepository.findByToken(notificationTokenAssignRequest.notificationToken())
                .ifPresentOrElse(
                        token -> token.updateMember(memberId),
                        () -> notificationTokenRepository.save(new NotificationToken(notificationTokenAssignRequest.notificationToken(), memberId))
                );
    }
}
