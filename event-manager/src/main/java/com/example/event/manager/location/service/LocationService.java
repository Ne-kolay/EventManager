package com.example.event.manager.location.service;

import com.example.event.manager.event.repository.EventRepository;
import com.example.event.manager.event.status.EventStatus;
import com.example.event.manager.location.domain.Location;
import com.example.event.manager.location.entity.LocationEntity;
import com.example.event.manager.location.mapper.LocationMapper;
import com.example.event.manager.location.repository.LocationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final EventRepository eventRepository;

    public LocationService(LocationRepository locationRepository,
                           LocationMapper locationMapper,
                           EventRepository eventRepository) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.eventRepository = eventRepository;
    }

    @Cacheable(cacheNames = "locations", key = "'id:' + #id")
    public Location getById(Long id) {
        LocationEntity locationEntity = locationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Location with id: " + id + " not found")
        );
        return locationMapper.toDomain(locationEntity);
    }

    public Location createLocation(Location location) {
        LocationEntity locationEntity = locationMapper.toEntity(location);
        LocationEntity savedLocationEntity = locationRepository.save(locationEntity);
        return locationMapper.toDomain(savedLocationEntity);
    }

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

    public Page<Location> getLocations(Pageable pageable) {
        return locationRepository.findAll(pageable)
                .map(locationMapper::toDomain);
    }

    @Cacheable(cacheNames = "locations", key = "'all'")
    public List<Location> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(locationMapper::toDomain)
                .toList();
    }

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
