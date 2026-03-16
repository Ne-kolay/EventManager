package com.example.event.manager.event.repository;

import com.example.event.manager.event.entity.EventEntity;
import com.example.event.manager.event.status.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    // Поиск по фильтрам
    @Query("""
            SELECT e FROM EventEntity e
            WHERE (:name IS NULL OR e.name LIKE %:name%)
            AND (:placesMin IS NULL OR e.maxPlaces >= :placesMin)
            AND (:placesMax IS NULL OR e.maxPlaces <= :placesMax)
            AND (CAST(:dateStartAfter AS date) IS NULL OR e.date >= :dateStartAfter)
            AND (CAST(:dateStartBefore AS date) IS NULL OR e.date <= :dateStartBefore)
            AND (:costMin IS NULL OR e.cost >= :costMin)
            AND (:costMax IS NULL OR e.cost <= :costMax)
            AND (:durationMin IS NULL OR e.duration >= :durationMin)
            AND (:durationMax IS NULL OR e.duration <= :durationMax)
            AND (:locationId IS NULL OR e.locationId = :locationId)
            AND (:eventStatus IS NULL OR e.status = :eventStatus)
            """)
    List<EventEntity> findEvents(
            @Param("name") String name,
            @Param("placesMin") Integer placesMin,
            @Param("placesMax") Integer placesMax,
            @Param("dateStartAfter") LocalDateTime dateStartAfter,
            @Param("dateStartBefore") LocalDateTime dateStartBefore,
            @Param("costMin") BigDecimal costMin,
            @Param("costMax") BigDecimal costMax,
            @Param("durationMin") Integer durationMin,
            @Param("durationMax") Integer durationMax,
            @Param("locationId") Long locationId,
            @Param("eventStatus") EventStatus eventStatus
    );

    List<EventEntity> findByOwnerId(Long ownerId);

    //SCHEDULER METHODS
    @Query("SELECT e.id FROM EventEntity e WHERE e.date <= :now AND e.status = 'WAIT_START'")
    List<Long> findStartedEventIds(@Param("now") LocalDateTime now);

    @Query(value = "SELECT id FROM events WHERE date + (duration * INTERVAL '1 minute') <= :now " +
            "AND status = 'STARTED'",
            nativeQuery = true)
    List<Long> findEndedEventIds(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE EventEntity e SET e.status = :status WHERE e.id IN :ids")
    void updateStatusForIds(@Param("ids") List<Long> ids, @Param("status") EventStatus status);
}