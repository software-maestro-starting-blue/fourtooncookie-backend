package com.startingblue.fourtooncookie.notification;

import com.startingblue.fourtooncookie.notification.dto.NotificationTokenAssignRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public final class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/notification")
    public void assignNotificationToken(final UUID memberId, @RequestBody final NotificationTokenAssignRequest notificationTokenAssignRequest) {
        notificationService.assignNotificationTokenToMember(memberId, notificationTokenAssignRequest);
    }
}
