package com.example.event.manager.event.controller;

import com.example.event.manager.event.dto.EventCreateRequestDTO;
import com.example.event.manager.event.dto.EventDTO;
import com.example.event.manager.event.dto.EventSearchDTO;
import com.example.event.manager.event.dto.EventUpdateRequestDTO;
import com.example.event.manager.event.mapper.EventMapper;
import com.example.event.manager.event.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestBody @Valid EventCreateRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventMapper.toDto(eventService.createEvent(dto)));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventMapper.toDto(eventService.getEventById(eventId)));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Long eventId,
            @RequestBody @Valid EventUpdateRequestDTO dto
    ) {
        return ResponseEntity.ok(eventMapper.toDto(eventService.updateEvent(eventId, dto)));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventDTO>> searchEvents(@RequestBody @Valid EventSearchDTO dto) {
        List<EventDTO> result = eventService.searchEvents(dto).stream()
                .map(eventMapper::toDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventDTO>> getMyEvents() {
        List<EventDTO> result = eventService.getMyEvents().stream()
                .map(eventMapper::toDto)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/registrations/{eventId}")
    public ResponseEntity<Void> registerForEvent(@PathVariable Long eventId) {
        eventService.registerForEvent(eventId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/registrations/cancel/{eventId}")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long eventId) {
        eventService.cancelRegistration(eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/registrations/my")
    public ResponseEntity<List<EventDTO>> getMyRegistrations() {
        List<EventDTO> result = eventService.getMyRegistrations().stream()
                .map(eventMapper::toDto)
                .toList();
        return ResponseEntity.ok(result);
    }
}
