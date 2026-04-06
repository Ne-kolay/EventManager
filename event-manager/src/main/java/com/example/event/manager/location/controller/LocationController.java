package com.example.event.manager.location.controller;

import com.example.event.manager.location.domain.Location;
import com.example.event.manager.location.dto.LocationCreateRequestDTO;
import com.example.event.manager.location.dto.LocationUpdateRequestDTO;
import com.example.event.manager.location.dto.LocationResponseDTO;
import com.example.event.manager.location.mapper.LocationMapper;
import com.example.event.manager.location.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;
    private final LocationMapper locationMapper;

    public LocationController(LocationService locationService, LocationMapper locationMapper) {
        this.locationService = locationService;
        this.locationMapper = locationMapper;
    }

    @GetMapping("/{id}")
    public LocationResponseDTO getLocationById(@PathVariable Long id) {
        Location location = locationService.getById(id);
        return locationMapper.toResponse(location);
    }

    @GetMapping
    public Page<LocationResponseDTO> getLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return locationService.getLocations(pageable)
                .map(locationMapper::toResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationResponseDTO createLocation(@RequestBody @Valid LocationCreateRequestDTO dto) {
        Location toCreate = locationMapper.toDomain(dto);
        Location created = locationService.createLocation(toCreate);
        return locationMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public LocationResponseDTO updateLocation(
            @PathVariable Long id,
            @RequestBody @Valid LocationUpdateRequestDTO dto
    ) {
        Location toUpdate = locationMapper.toDomain(dto);
        Location updated = locationService.updateLocation(id, toUpdate);
        return locationMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocationById(@PathVariable Long id) {
        locationService.deleteLocation(id);
    }
}
