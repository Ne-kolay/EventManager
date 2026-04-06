package com.example.eventnotificator.notification.controller;

import com.example.eventnotificator.notification.dto.MarkAsReadRequestDTO;
import com.example.eventnotificator.notification.dto.NotificationResponseDTO;
import com.example.eventnotificator.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications() {
        return ResponseEntity.ok(notificationService.getUnreadNotifications());
    }

    @PostMapping
    public ResponseEntity<Void> markAsRead(@RequestBody MarkAsReadRequestDTO request) {
        notificationService.markAsRead(request);
        return ResponseEntity.ok().build();
    }
}
