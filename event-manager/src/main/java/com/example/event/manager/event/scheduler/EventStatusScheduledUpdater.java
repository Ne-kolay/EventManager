package com.example.event.manager.event.scheduler;

import com.example.event.manager.event.entity.EventEntity;
import com.example.event.manager.event.entity.RegistrationEntity;
import com.example.event.manager.event.repository.EventRepository;
import com.example.event.manager.event.repository.RegistrationRepository;
import com.example.event.manager.event.status.EventStatus;
import com.example.event.manager.kafka.EventChangeSender;
import com.example.eventcommon.kafka.ChangeItem;
import com.example.eventcommon.kafka.EventChangeKafkaMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class EventStatusScheduledUpdater {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final EventChangeSender eventChangeSender;

    public EventStatusScheduledUpdater(EventRepository eventRepository,
                                       RegistrationRepository registrationRepository,
                                       EventChangeSender eventChangeSender) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.eventChangeSender = eventChangeSender;
    }

    @Scheduled(fixedRateString = "${scheduler.rate}", initialDelayString = "${scheduler.delay}")
    @Transactional
    public void updateStatuses() {
        LocalDateTime now = LocalDateTime.now();
        processEvents(eventRepository.findStartedEvents(now), EventStatus.WAIT_START, EventStatus.STARTED);
        processEvents(eventRepository.findEndedEvents(now), EventStatus.STARTED, EventStatus.FINISHED);
    }

    private void processEvents(List<EventEntity> events, EventStatus oldStatus, EventStatus newStatus) {
        if (events.isEmpty()) return;

        List<Long> eventIds = events.stream().map(EventEntity::getId).toList();
        Map<Long, List<Long>> subscribersByEventId = new HashMap<>();
        for (RegistrationEntity r : registrationRepository.findByEventIdIn(eventIds)) {
            subscribersByEventId
                    .computeIfAbsent(r.getEvent().getId(), k -> new ArrayList<>())
                    .add(r.getUserId());
        }

        for (EventEntity event : events) {
            event.setStatus(newStatus);
            eventRepository.save(event);

            List<Long> subscribers = new ArrayList<>(
                    subscribersByEventId.getOrDefault(event.getId(), new ArrayList<>())
            );
            if (!subscribers.contains(event.getOwnerId())) {
                subscribers.add(event.getOwnerId());
            }

            eventChangeSender.send(new EventChangeKafkaMessage(
                    UUID.randomUUID(),
                    "EVENT_UPDATED",
                    event.getId(),
                    LocalDateTime.now(),
                    event.getOwnerId(),
                    null,
                    subscribers,
                    List.of(new ChangeItem("status", oldStatus.name(), newStatus.name()))
            ));
        }
    }
}
