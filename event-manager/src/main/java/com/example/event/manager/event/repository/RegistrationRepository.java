package com.example.event.manager.event.repository;

import com.example.event.manager.event.entity.RegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    void deleteByEventIdAndUserId(Long eventId, Long userId);

    List<RegistrationEntity> findByUserId(Long userId);

    List<RegistrationEntity> findByEventId(Long eventId);

    List<RegistrationEntity> findByEventIdIn(List<Long> eventIds);
}
