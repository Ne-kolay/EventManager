package com.example.event.manager.user.dto;

import com.example.event.manager.common.Role;

public record UserDTO(
        Long id,
        String login,
        int age,
        Role role
) { }
