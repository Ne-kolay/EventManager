package com.example.eventnotificator.notification.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MarkAsReadRequestDTO(
        @NotNull List<Long> notificationIds
) {}
