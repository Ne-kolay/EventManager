package com.example.Event.manager.location.dto;

import jakarta.validation.constraints.*;

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
