package com.example.event.manager.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserCredentials(
        @NotBlank String login,
        @NotBlank String password
) { }

