package com.example.event.manager.event.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventUpdateRequestDTO(
        @Size(min = 1) String name,
        @Min(1) Integer maxPlaces,
        @Future LocalDateTime date,
        @DecimalMin("0") BigDecimal cost,
        @Min(30) Integer duration,
        Long locationId
) { }
