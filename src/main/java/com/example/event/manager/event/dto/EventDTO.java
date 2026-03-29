package com.example.event.manager.event.dto;

import com.example.event.manager.event.status.EventStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventDTO(
        Long id,
        String name,
        Long ownerId,
        Integer maxPlaces,
        Integer occupiedPlaces,
        LocalDateTime date,
        BigDecimal cost,
        Integer duration,
        Long locationId,
        EventStatus status
) { }
