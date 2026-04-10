package com.example.eventnotificator.notification.repository;

import com.example.eventnotificator.notification.entity.NotificationEventPayloadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationEventPayloadRepository extends JpaRepository<NotificationEventPayloadEntity, Long> {

    Optional<NotificationEventPayloadEntity> findByMessageId(UUID messageId);

    @Modifying
    @Query("DELETE FROM NotificationEventPayloadEntity p WHERE p.id NOT IN (SELECT n.payload.id FROM NotificationEntity n)")
    void deleteOrphaned();
}
