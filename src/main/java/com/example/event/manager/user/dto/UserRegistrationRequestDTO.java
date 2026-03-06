package com.example.event.manager.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRegistrationRequestDTO(
        @NotBlank String login,
        @NotBlank String password,
        int age
) { }


