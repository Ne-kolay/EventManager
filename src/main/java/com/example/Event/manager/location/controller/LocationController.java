package com.example.Event.manager.location.controller;

import com.example.Event.manager.location.domain.*;
import com.example.Event.manager.location.dto.*;
import com.example.Event.manager.location.mapper.*;
import com.example.Event.manager.location.repository.*;
import com.example.Event.manager.location.service.*;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class LocationController {

    private final LocationService locationService;
    private final LocationMapper locationMapper;

    @Autowired
    public LocationController(LocationService locationService, LocationMapper locationMapper) {
        this.locationService = locationService;
        this.locationMapper = locationMapper;
    }

    @GetMapping("/locations/{id}")
    public LocationDTO getLocationById(@PathVariable Long id){
        Location location = locationService.getById(id);
        return locationMapper.toDto(location);
    }
    @GetMapping("/locations")
    public List<LocationDTO> getAllLocations() {
        return locationService.getAllLocations().stream()
                .map(locationMapper::toDto)
                .toList();
    }
    @PostMapping("/locations")
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDTO createLocation(@RequestBody @Valid LocationDTO locationDTO){
        Location locationToCreate = locationService.createLocation(locationMapper.toDomain(locationDTO));
        return locationMapper.toDto(locationToCreate);
    }
    @PutMapping("/locations/{id}")
    public LocationDTO updateLocation(@PathVariable Long id,
                                      @RequestBody @Valid LocationDTO locationDTO){
        Location locationToUpdate = locationMapper.toDomain(locationDTO);
        Location updated = locationService.updateLocation(id, locationToUpdate);
        return locationMapper.toDto(updated);
    }
    @DeleteMapping("/locations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocationById(@PathVariable Long id){
        locationService.deleteLocation(id);
    }
}
