package com.example.eventnotificator.notification.repository;

import com.example.eventnotificator.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("SELECT n FROM NotificationEntity n JOIN FETCH n.payload WHERE n.userId = :userId AND n.isRead = false")
    List<NotificationEntity> findByUserIdAndIsReadFalse(@Param("userId") Long userId);

    List<NotificationEntity> findByIdInAndUserId(List<Long> ids, Long userId);

    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.createdAt < :cutoff")
    void deleteByCreatedAtBefore(@Param("cutoff") LocalDateTime cutoff);
}
