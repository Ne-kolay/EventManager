package com.example.event.manager.event.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventCreateRequestDTO(
        @NotBlank String name,
        @Min(1) Integer maxPlaces,
        @Future LocalDateTime date,
        @DecimalMin("0") BigDecimal cost,
        @Min(30) Integer duration,
        @NotNull Long locationId
) { }
