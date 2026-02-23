package com.example.Event.manager.location.service;

import com.example.Event.manager.location.domain.*;
import com.example.Event.manager.location.entity.*;
import com.example.Event.manager.location.mapper.*;
import com.example.Event.manager.location.repository.*;
import jakarta.persistence.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    public LocationService(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

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
    public List<Location> getAllLocations() {
        return locationRepository.findAll()
                .stream()
                .map((locationMapper::toDomain))
                .toList();
    }
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException("Location with id: " + id + " not found");
        }
        locationRepository.deleteById(id);
    }
}
