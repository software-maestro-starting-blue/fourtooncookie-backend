package com.startingblue.fourtooncookie.notification;

import com.startingblue.fourtooncookie.diary.domain.Diary;
import com.startingblue.fourtooncookie.notification.dto.NotificationTokenAssignRequest;

import java.util.UUID;

public interface NotificationService {
    void assignNotificationTokenToMember(final UUID memberId, final NotificationTokenAssignRequest notificationTokenAssignRequest);

    void sendNotificationToMember(final UUID memberId, final Diary diary);
}
