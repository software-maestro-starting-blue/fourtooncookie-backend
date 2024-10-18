package com.startingblue.fourtooncookie.notification;

import com.startingblue.fourtooncookie.notification.domain.NotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {
    List<NotificationToken> findByMemberId(UUID memberId);

    Optional<NotificationToken> findByToken(String token);

    void deleteByModifiedDateTimeBefore(LocalDateTime dateTime);

    Optional<NotificationToken> findByMemberIdAndToken(UUID memberId, String token);
}
