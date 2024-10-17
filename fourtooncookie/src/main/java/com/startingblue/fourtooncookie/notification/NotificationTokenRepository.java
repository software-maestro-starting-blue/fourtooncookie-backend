package com.startingblue.fourtooncookie.notification;

import com.startingblue.fourtooncookie.notification.domain.NotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {
}
