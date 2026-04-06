package com.example.eventnotificator.notification.dto;

import java.util.List;

public record MarkAsReadRequestDTO(
        List<Long> notificationIds
) {}
