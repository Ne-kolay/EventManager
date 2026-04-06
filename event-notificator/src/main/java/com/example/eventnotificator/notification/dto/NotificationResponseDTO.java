package com.example.eventnotificator.notification.dto;

import java.time.LocalDateTime;

public record NotificationResponseDTO(
        Long notificationId,
        String type,
        Long eventId,
        LocalDateTime createdAt,
        boolean isRead,
        String message,
        Object payload
) {}
