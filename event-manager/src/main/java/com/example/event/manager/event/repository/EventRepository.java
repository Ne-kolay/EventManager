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

    // Search with filters
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

    boolean existsByLocationIdAndStatusNot(Long locationId, EventStatus status);

    @Query(value = """
            SELECT COUNT(*) > 0 FROM events
            WHERE location_id = :locationId
            AND id != :excludeId 
            AND status != 'CANCELLED'
            AND date < :end
            AND date + (duration * INTERVAL '1 minute') > :start
            """, nativeQuery = true)
    boolean existsOverlappingEvent(
            @Param("locationId") Long locationId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("excludeId") Long excludeId //for excluding the updating event itself
    );

    //SCHEDULER METHODS
    @Query("SELECT e FROM EventEntity e WHERE e.date <= :now AND e.status = 'WAIT_START'")
    List<EventEntity> findStartedEvents(@Param("now") LocalDateTime now);

    @Query(value = "SELECT * FROM events WHERE date + (duration * INTERVAL '1 minute') <= :now " +
            "AND status = 'STARTED'",
            nativeQuery = true)
    List<EventEntity> findEndedEvents(@Param("now") LocalDateTime now);
}