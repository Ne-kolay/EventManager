package com.example.event.manager.location.service;

import com.example.event.manager.location.domain.Location;
import com.example.event.manager.location.mapper.LocationMapper;
import com.example.event.manager.location.repository.LocationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationCacheService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public LocationCacheService(LocationRepository locationRepository,
                                LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    @Cacheable(cacheNames = "locations", key = "'all'")
    public List<Location> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(locationMapper::toDomain)
                .toList();
    }

    @Cacheable(cacheNames = "locations", key = "'id:' + #id")
    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .map(locationMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("Location with id: " + id + " not found"));
    }
}
