package com.example.Event.manager.exceptions;

import java.time.LocalDateTime;

public record ErrorMessageResponse(
        String message,
        int status,
        String path,
        LocalDateTime timestamp
) {}

