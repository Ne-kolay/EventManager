package com.example.eventnotificator.notification.repository;

import com.example.eventnotificator.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUserIdAndIsReadFalse(Long userId);

    List<NotificationEntity> findByIdInAndUserId(List<Long> ids, Long userId);

    void deleteByCreatedAtBefore(LocalDateTime cutoff);
}
