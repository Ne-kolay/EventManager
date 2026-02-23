package com.example.Event.manager.location.dto;

public record LocationDTO (
        Long id,
        String name,
        String address,
        Integer capacity,
        String description
) {}
