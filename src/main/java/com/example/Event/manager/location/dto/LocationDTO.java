package com.example.Event.manager.location.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

public record LocationDTO (
        Long id,

        @NotBlank
        String name,

        @NotBlank
        String address,

        @NotNull
        @Min(5)
        Integer capacity,

        String description
) {}
