package com.example.event.manager.event.service;

import com.example.event.manager.event.domain.Event;
import com.example.event.manager.event.mapper.EventMapper;
import com.example.event.manager.event.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class EventCacheService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventCacheService(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Cacheable(cacheNames = "events", key = "'id:' + #id")
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .map(eventMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
    }
}
