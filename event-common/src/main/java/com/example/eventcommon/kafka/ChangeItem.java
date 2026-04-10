package com.example.eventcommon.kafka;

public record ChangeItem(
        String field,
        Object oldValue,
        Object newValue
) {}
