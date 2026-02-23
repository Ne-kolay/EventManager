package com.example.Event.manager.location.repository;

import com.example.Event.manager.location.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
}
