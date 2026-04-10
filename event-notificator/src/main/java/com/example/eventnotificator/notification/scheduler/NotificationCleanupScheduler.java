package com.example.eventnotificator.notification.scheduler;

import com.example.eventnotificator.notification.repository.NotificationEventPayloadRepository;
import com.example.eventnotificator.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class NotificationCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationCleanupScheduler.class);

    private final NotificationRepository notificationRepository;
    private final NotificationEventPayloadRepository payloadRepository;

    public NotificationCleanupScheduler(NotificationRepository notificationRepository,
                                        NotificationEventPayloadRepository payloadRepository) {
        this.notificationRepository = notificationRepository;
        this.payloadRepository = payloadRepository;
    }

    @Scheduled(fixedRateString = "${scheduler.rate}", initialDelayString = "${scheduler.delay}")
    @Transactional
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);

        notificationRepository.deleteByCreatedAtBefore(cutoff);

        payloadRepository.deleteOrphaned();

        log.info("Notification cleanup done, cutoff={}", cutoff);
    }
}
