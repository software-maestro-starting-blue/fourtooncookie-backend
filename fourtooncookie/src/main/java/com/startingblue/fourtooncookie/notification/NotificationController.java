package com.startingblue.fourtooncookie.notification;

import com.startingblue.fourtooncookie.notification.dto.NotificationTokenAssignRequest;
import com.startingblue.fourtooncookie.notification.dto.NotificationTokenUnassignRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public final class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/notification")
    public void assignNotificationToken(final Locale locale, final UUID memberId, @RequestBody final NotificationTokenAssignRequest notificationTokenAssignRequest) {
        notificationService.assignNotificationTokenToMember(locale, memberId, notificationTokenAssignRequest);
    }

    @DeleteMapping("/notification")
    public void unassignNotificationToken(final UUID memberId, @RequestBody final NotificationTokenUnassignRequest notificationTokenAssignRequest) {
        notificationService.removeNotificationTokenFromMember(memberId, notificationTokenAssignRequest);
    }
}
