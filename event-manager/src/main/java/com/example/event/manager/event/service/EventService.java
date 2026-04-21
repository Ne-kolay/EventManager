package com.example.event.manager.event.service;

import com.example.event.manager.common.Role;
import com.example.event.manager.event.domain.Event;
import com.example.event.manager.event.dto.EventCreateRequestDTO;
import com.example.event.manager.event.dto.EventSearchDTO;
import com.example.event.manager.event.dto.EventUpdateRequestDTO;
import com.example.event.manager.event.entity.EventEntity;
import com.example.event.manager.event.entity.RegistrationEntity;
import com.example.event.manager.event.mapper.EventMapper;
import com.example.event.manager.event.repository.EventRepository;
import com.example.event.manager.event.repository.RegistrationRepository;
import com.example.event.manager.event.status.EventStatus;
import com.example.event.manager.exceptions.ForbiddenException;
import com.example.event.manager.kafka.EventChangeSender;
import com.example.event.manager.location.entity.LocationEntity;
import com.example.event.manager.location.repository.LocationRepository;
import com.example.event.manager.user.entity.UserEntity;
import com.example.event.manager.user.repository.UserRepository;
import com.example.eventcommon.kafka.ChangeItem;
import com.example.eventcommon.kafka.EventChangeKafkaMessage;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final EventChangeSender eventChangeSender;
    private final EventCacheService eventCacheService;
    private final Logger log = LoggerFactory.getLogger(EventService.class);

    public EventService(EventRepository eventRepository,
                        RegistrationRepository registrationRepository,
                        LocationRepository locationRepository,
                        UserRepository userRepository,
                        EventMapper eventMapper,
                        EventChangeSender eventChangeSender,
                        EventCacheService eventCacheService) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
        this.eventChangeSender = eventChangeSender;
        this.eventCacheService = eventCacheService;
    }

    public Event createEvent(EventCreateRequestDTO dto) {
        LocationEntity location = locationRepository.findById(dto.locationId())
                .orElseThrow(() -> new EntityNotFoundException("Location with id " + dto.locationId() + " not found"));

        if (dto.maxPlaces() > location.getCapacity()) {
            throw new IllegalStateException(
                    "maxPlaces (" + dto.maxPlaces() + ") exceeds location capacity (" + location.getCapacity() + ")"
            );
        }

        LocalDateTime start = dto.date();
        LocalDateTime end = dto.date().plusMinutes(dto.duration());
        if (eventRepository.existsOverlappingEvent(dto.locationId(), start, end, -1L /* ничего не исключаем */)) {
            throw new IllegalStateException("Location is already booked for this time slot");
        }

        Event event = eventMapper.toDomain(dto);
        event.setOwnerId(getCurrentUser().getId());
        event.setStatus(EventStatus.WAIT_START);

        EventEntity saved = eventRepository.save(eventMapper.toEntity(event));
        return eventMapper.toDomain(saved);
    }

    public Event getEventById(Long id) {
        try {
            return eventCacheService.getEventById(id);
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis is unavailable, fallback to DB for event {}", id, ex);
            return eventRepository.findById(id)
                    .map(eventMapper::toDomain)
                    .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
        }
    }

    public Page<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(eventMapper::toDomain);
    }

    public List<Event> searchEvents(EventSearchDTO filter) {
        return eventRepository.findEvents(
                filter.name(),
                filter.placesMin(),
                filter.placesMax(),
                filter.dateStartAfter(),
                filter.dateStartBefore(),
                filter.costMin(),
                filter.costMax(),
                filter.durationMin(),
                filter.durationMax(),
                filter.locationId(),
                filter.eventStatus()
        ).stream().map(eventMapper::toDomain).toList();
    }

    @Transactional
    @CacheEvict(cacheNames = "events", key = "'id:' + #id")
    public Event updateEvent(Long id, EventUpdateRequestDTO dto) {
        EventEntity entity = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));

        checkOwnerOrAdmin(entity);

        Long locationId = dto.locationId() != null ? dto.locationId() : entity.getLocationId();
        LocationEntity location = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location with id " + locationId + " not found"));

        if (dto.date() != null || dto.duration() != null || dto.locationId() != null) {
            LocalDateTime start = dto.date() != null ? dto.date() : entity.getDate();
            Integer duration = dto.duration() != null ? dto.duration() : entity.getDuration();
            LocalDateTime end = start.plusMinutes(duration);
            if (eventRepository.existsOverlappingEvent(locationId, start, end, entity.getId())) {
                throw new IllegalStateException("Location is already booked for this time slot");
            }
        }

        List<ChangeItem> changes = new ArrayList<>();
        addIfChanged(changes, "name",       entity.getName(),       dto.name());
        addIfChanged(changes, "date",       entity.getDate(),       dto.date());
        addIfChanged(changes, "cost",       entity.getCost(),       dto.cost());
        addIfChanged(changes, "duration",   entity.getDuration(),   dto.duration());
        addIfChanged(changes, "locationId", entity.getLocationId(), dto.locationId());
        addIfChanged(changes, "maxPlaces",  entity.getMaxPlaces(),  dto.maxPlaces());

        if (dto.name() != null) entity.setName(dto.name());
        if (dto.date() != null) entity.setDate(dto.date());
        if (dto.cost() != null) entity.setCost(dto.cost());
        if (dto.duration() != null) entity.setDuration(dto.duration());
        if (dto.locationId() != null) entity.setLocationId(dto.locationId());

        if (dto.maxPlaces() != null) {
            if (dto.maxPlaces() < entity.getOccupiedPlaces()) {
                throw new IllegalStateException(
                        "New maxPlaces (" + dto.maxPlaces() + ") is less than already occupied places ("
                                + entity.getOccupiedPlaces() + ")"
                );
            }
            if (dto.maxPlaces() > location.getCapacity()) {
                throw new IllegalStateException(
                        "maxPlaces (" + dto.maxPlaces() + ") exceeds location capacity (" + location.getCapacity() + ")"
                );
            }
            entity.setMaxPlaces(dto.maxPlaces());
        }

        EventEntity saved = eventRepository.save(entity);

        if (!changes.isEmpty()) {
            publishEventChange(saved, getCurrentUser().getId(), changes);
        }

        return eventMapper.toDomain(saved);
    }

    @Transactional
    @CacheEvict(cacheNames = "events", key = "'id:' + #id")
    public void deleteEvent(Long id) {
        EventEntity entity = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
        checkOwnerOrAdmin(entity);
        if (entity.getStatus() != EventStatus.WAIT_START) {
            throw new IllegalStateException("Cannot cancel event with status: " + entity.getStatus());
        }
        entity.setStatus(EventStatus.CANCELLED);
        eventRepository.save(entity);

        publishEventChange(entity, getCurrentUser().getId(),
                List.of(new ChangeItem("status", EventStatus.WAIT_START.name(), EventStatus.CANCELLED.name())));
    }

    @Transactional
    @CacheEvict(cacheNames = "events", key = "'id:' + #eventId")
    public void registerForEvent(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        if (event.getStatus() == EventStatus.FINISHED || event.getStatus() == EventStatus.CANCELLED) {
            throw new IllegalStateException("Cannot register for event with status: " + event.getStatus());
        }
        if (event.getOccupiedPlaces() >= event.getMaxPlaces()) {
            throw new IllegalStateException("Event is full");
        }

        Long userId = getCurrentUser().getId();
        if (registrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new IllegalStateException("User is already registered for this event");
        }

        RegistrationEntity registration = new RegistrationEntity(null, event, userId);
        registrationRepository.save(registration);
        event.setOccupiedPlaces(event.getOccupiedPlaces() + 1);
        eventRepository.save(event);
    }

    @Transactional
    @CacheEvict(cacheNames = "events", key = "'id:' + #eventId")
    public void cancelRegistration(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        if (event.getStatus() != EventStatus.WAIT_START) {
            throw new IllegalStateException("Cannot cancel registration for event with status: " + event.getStatus());
        }

        Long userId = getCurrentUser().getId();
        if (!registrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new IllegalStateException("User is not registered for this event");
        }

        registrationRepository.deleteByEventIdAndUserId(eventId, userId);
        event.setOccupiedPlaces(event.getOccupiedPlaces() - 1);
        eventRepository.save(event);
    }

    public List<Event> getMyEvents() {
        Long userId = getCurrentUser().getId();
        List<EventEntity> entities = eventRepository.findByOwnerId(userId);
        return entities.stream()
                .map(entity -> eventMapper.toDomain(entity))
                .toList();
    }

    public List<Event> getMyRegistrations() {
        Long userId = getCurrentUser().getId();
        return registrationRepository.findByUserId(userId).stream()
                .map(registration -> eventMapper.toDomain(registration.getEvent()))
                .toList();
    }

    private void publishEventChange(EventEntity entity, Long changedById, List<ChangeItem> changes) {
        List<Long> subscribers = registrationRepository.findByEventId(entity.getId()).stream()
                .map(RegistrationEntity::getUserId)
                .collect(Collectors.toList());
        if (!subscribers.contains(entity.getOwnerId())) {
            subscribers.add(entity.getOwnerId());
        }
        eventChangeSender.send(new EventChangeKafkaMessage(
                UUID.randomUUID(),
                "EVENT_UPDATED",
                entity.getId(),
                LocalDateTime.now(),
                entity.getOwnerId(),
                changedById,
                subscribers,
                changes
        ));
    }

    private void addIfChanged(List<ChangeItem> changes, String field, Object oldVal, Object newVal) {
        if (newVal != null && !newVal.equals(oldVal)) {
            changes.add(new ChangeItem(field, oldVal, newVal));
        }
    }

    private UserEntity getCurrentUser() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + login));
    }

    private void checkOwnerOrAdmin(EventEntity event) {
        UserEntity currentUser = getCurrentUser();
        boolean isOwner = event.getOwnerId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You don't have permission to modify this event");
        }
    }
}
