package com.example.event.manager.event.dto;

import com.example.event.manager.event.status.EventStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventSearchDTO(
        String name,
        Integer placesMin,
        Integer placesMax,
        LocalDateTime dateStartAfter,
        LocalDateTime dateStartBefore,
        BigDecimal costMin,
        BigDecimal costMax,
        Integer durationMin,
        Integer durationMax,
        Long locationId,
        EventStatus eventStatus
) { }
