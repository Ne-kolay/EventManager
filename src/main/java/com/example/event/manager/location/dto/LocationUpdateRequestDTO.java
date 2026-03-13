package com.example.event.manager.location.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record LocationUpdateRequestDTO(
        @NotBlank String name,
        @NotBlank String address,
        @NotNull @Min(5) Integer capacity,
        String description
) {}

