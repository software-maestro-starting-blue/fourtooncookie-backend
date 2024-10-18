package com.startingblue.fourtooncookie.notification;

import com.startingblue.fourtooncookie.notification.dto.NotificationTokenAssignRequest;

import java.util.UUID;

public interface NotificationService {
    void assignNotificationTokenToMember(final UUID memberId, final NotificationTokenAssignRequest notificationTokenAssignRequest);

    private final NotificationTokenRepository notificationTokenRepository;

    public void assignNotificationTokenToMember(final UUID memberId, final NotificationTokenAssignRequest notificationTokenAssignRequest) {
        notificationTokenRepository.findByToken(notificationTokenAssignRequest.notificationToken())
                .ifPresentOrElse(
                        token -> token.updateMember(memberId),
                        () -> notificationTokenRepository.save(new NotificationToken(notificationTokenAssignRequest.notificationToken(), memberId))
                );
    }
}
