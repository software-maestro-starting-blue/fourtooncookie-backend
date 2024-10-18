package com.startingblue.fourtooncookie.notification.batch;

import com.startingblue.fourtooncookie.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class NotificationBatchService {

    private final NotificationService notificationService;

    @Scheduled(cron = "${cleanup.cron}")
    public void schedule() {
        notificationService.cleanupOldRecords();
    }
}
