package com.example.eventnotificator.notification.service;

import com.example.eventnotificator.notification.dto.MarkAsReadRequestDTO;
import com.example.eventnotificator.notification.dto.NotificationResponseDTO;
import com.example.eventnotificator.notification.entity.NotificationEntity;
import com.example.eventnotificator.notification.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;
    private final NotificationCounterService notificationCounterService;

    public NotificationService(NotificationRepository notificationRepository,
                               ObjectMapper objectMapper, NotificationCounterService notificationCounterService) {
        this.notificationRepository = notificationRepository;
        this.objectMapper = objectMapper;
        this.notificationCounterService = notificationCounterService;
    }

    public List<NotificationResponseDTO> getUnreadNotifications() {
        Long userId = getCurrentUserId();
        return notificationRepository.findByUserIdAndIsReadFalse(userId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public void markAsRead(MarkAsReadRequestDTO request) {
        Long userId = getCurrentUserId();
        List<NotificationEntity> notifications =
                notificationRepository.findByIdInAndUserId(request.notificationIds(), userId);

        LocalDateTime now = LocalDateTime.now();
        notifications.forEach(n -> {
            n.setRead(true);
            n.setReadAt(now);
        });
        notificationRepository.saveAll(notifications);
        notificationCounterService.syncUnreadFromDatabase(userId);
    }

    private NotificationResponseDTO toResponseDTO(NotificationEntity entity) {
        Object payload = null;
        try {
            payload = objectMapper.readValue(entity.getPayload().getPayloadJson(), Object.class);
        } catch (Exception e) {
            log.error("Failed to deserialize payload for notificationId={}", entity.getId(), e);
        }

        return new NotificationResponseDTO(
                entity.getId(),
                entity.getPayload().getEventType(),
                entity.getPayload().getEventId(),
                entity.getCreatedAt(),
                entity.isRead(),
                buildMessage(entity),
                payload
        );
    }

    private String buildMessage(NotificationEntity entity) {
        return "Event #" + entity.getPayload().getEventId() + " was changed";
    }

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }
}
