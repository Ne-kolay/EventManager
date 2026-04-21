package com.example.eventnotificator.notification.kafka;

import com.example.eventcommon.kafka.EventChangeKafkaMessage;
import com.example.eventnotificator.notification.entity.NotificationEntity;
import com.example.eventnotificator.notification.entity.NotificationEventPayloadEntity;
import com.example.eventnotificator.notification.repository.NotificationEventPayloadRepository;
import com.example.eventnotificator.notification.repository.NotificationRepository;
import com.example.eventnotificator.notification.service.NotificationCounterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventChangeConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventChangeConsumer.class);

    private final NotificationEventPayloadRepository payloadRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationCounterService notificationCounterService;
    private final ObjectMapper objectMapper;

    public EventChangeConsumer(NotificationEventPayloadRepository payloadRepository,
                               NotificationRepository notificationRepository, NotificationCounterService notificationCounterService,
                               ObjectMapper objectMapper) {
        this.payloadRepository = payloadRepository;
        this.notificationRepository = notificationRepository;
        this.notificationCounterService = notificationCounterService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "event-changes", groupId = "event-notificator")
    @Transactional
    public void consume(EventChangeKafkaMessage message) {
        if (payloadRepository.findByMessageId(message.messageId()).isPresent()) {
            log.info("Message already processed, skipping: messageId={}", message.messageId());
            return;
        }

        NotificationEventPayloadEntity payload = new NotificationEventPayloadEntity();
        payload.setMessageId(message.messageId());
        payload.setEventType(message.eventType());
        payload.setEventId(message.eventId());
        payload.setOccurredAt(message.occurredAt());
        payload.setOwnerId(message.ownerId());
        payload.setChangedById(message.changedById());
        payload.setPayloadJson(buildPayloadJson(message));
        NotificationEventPayloadEntity savedPayload = payloadRepository.save(payload);

        List<NotificationEntity> notifications = message.subscribers().stream()
                .map(userId -> {
                    NotificationEntity n = new NotificationEntity();
                    n.setUserId(userId);
                    n.setPayload(savedPayload);
                    n.setCreatedAt(LocalDateTime.now());
                    return n;
                })
                .toList();
        notificationRepository.saveAll(notifications);
        message.subscribers().forEach(userId ->
                notificationCounterService.incrementUnread(userId, 1));

        log.info("Processed event change: eventId={}, subscribers={}",
                message.eventId(), message.subscribers().size());
    }

    private String buildPayloadJson(EventChangeKafkaMessage message) {
        try {
            return objectMapper.writeValueAsString(new PayloadDto(
                    message.eventId(),
                    message.changedById(),
                    message.changes()
            ));
        } catch (Exception e) {
            log.error("Failed to serialize payload for messageId={}", message.messageId(), e);
            return "{}";
        }
    }

    private record PayloadDto(
            Long eventId,
            Long changedById,
            Object changes
    ) {}
}
