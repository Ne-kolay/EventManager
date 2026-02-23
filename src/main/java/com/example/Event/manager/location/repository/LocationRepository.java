package com.example.Event.manager.location.repository;

import com.example.Event.manager.location.domain.*;
import com.example.Event.manager.location.entity.*;
import org.springframework.data.jpa.repository.*;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
}
