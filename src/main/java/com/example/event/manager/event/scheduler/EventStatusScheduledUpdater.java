package com.example.event.manager.event.scheduler;

import com.example.event.manager.event.repository.EventRepository;
import com.example.event.manager.event.status.EventStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventStatusScheduledUpdater {

    private final EventRepository eventRepository;

    public EventStatusScheduledUpdater(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(fixedRate = 60000, initialDelay = 60000)
    @Transactional
    public void updateStatuses() {
        LocalDateTime now = LocalDateTime.now();

        List<Long> startedIds = eventRepository.findStartedEventIds(now);
        if (!startedIds.isEmpty()) {
            eventRepository.updateStatusForIds(startedIds, EventStatus.STARTED);
        }

        List<Long> finishedIds = eventRepository.findEndedEventIds(now);
        if (!finishedIds.isEmpty()) {
            eventRepository.updateStatusForIds(finishedIds, EventStatus.FINISHED);
        }
    }
}
