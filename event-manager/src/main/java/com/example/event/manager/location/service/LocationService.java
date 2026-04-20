package com.example.event.manager.location.service;

import com.example.event.manager.event.repository.EventRepository;
import com.example.event.manager.event.status.EventStatus;
import com.example.event.manager.location.domain.Location;
import com.example.event.manager.location.entity.LocationEntity;
import com.example.event.manager.location.mapper.LocationMapper;
import com.example.event.manager.location.repository.LocationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final EventRepository eventRepository;
    private final LocationCacheService locationCacheService;
    private final Logger log = LoggerFactory.getLogger(LocationService.class);

    public LocationService(LocationRepository locationRepository,
                           LocationMapper locationMapper,
                           EventRepository eventRepository,
                           LocationCacheService locationCacheService) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.eventRepository = eventRepository;
        this.locationCacheService = locationCacheService;
    }

    public Location getLocationById(Long id) {
        try {
            return locationCacheService.getLocationById(id);
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis is unavailable, fallback to DB for location {}", id, ex);
            return locationRepository.findById(id)
                    .map(locationMapper::toDomain)
                    .orElseThrow(() -> new EntityNotFoundException("Location with id: " + id + " not found"));
        }
    }

    public Page<Location> getLocations(Pageable pageable) {
        return locationRepository.findAll(pageable)
                .map(locationMapper::toDomain);
    }

    public List<Location> getAllLocations() {
        try {
            return locationCacheService.getAllLocations();
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis is unavailable, fallback to DB for locations", ex);
            return locationRepository.findAll().stream()
                    .map(locationMapper::toDomain)
                    .toList();
        }
    }

    @CacheEvict(cacheNames = "locations", key = "'all'")
    public Location createLocation(Location location) {
        LocationEntity locationEntity = locationMapper.toEntity(location);
        LocationEntity savedLocationEntity = locationRepository.save(locationEntity);
        return locationMapper.toDomain(savedLocationEntity);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "locations", key = "'all'"),
            @CacheEvict(cacheNames = "locations", key = "'id:' + #id")
    })
    public Location updateLocation(Long id, Location location) {
        LocationEntity locationToUpdate = locationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Location with id: " + id + " not found"));
        locationToUpdate.setName(location.getName());
        locationToUpdate.setAddress(location.getAddress());
        locationToUpdate.setCapacity(location.getCapacity());
        locationToUpdate.setDescription(location.getDescription());
        LocationEntity saved = locationRepository.save(locationToUpdate);
        return locationMapper.toDomain(saved);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "locations", key = "'all'"),
            @CacheEvict(cacheNames = "locations", key = "'id:' + #id")
    })
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException("Location with id: " + id + " not found");
        }
        if (eventRepository.existsByLocationIdAndStatusNot(id, EventStatus.CANCELLED)) {
            throw new IllegalStateException("Cannot delete location with active events");
        }
        locationRepository.deleteById(id);
    }
}
