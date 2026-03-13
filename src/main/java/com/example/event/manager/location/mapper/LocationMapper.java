package com.example.event.manager.location.mapper;

import com.example.event.manager.location.domain.Location;
import com.example.event.manager.location.dto.LocationCreateRequestDTO;
import com.example.event.manager.location.dto.LocationUpdateRequestDTO;
import com.example.event.manager.location.dto.LocationResponseDTO;
import com.example.event.manager.location.entity.LocationEntity;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    // CreateRequestDTO → Domain
    public Location toDomain(LocationCreateRequestDTO dto) {
        if (dto == null) return null;

        return new Location(
                null,
                dto.name(),
                dto.address(),
                dto.capacity(),
                dto.description()
        );
    }

    // UpdateRequestDTO → Domain
    public Location toDomain(LocationUpdateRequestDTO dto) {
        if (dto == null) return null;

        return new Location(
                null,
                dto.name(),
                dto.address(),
                dto.capacity(),
                dto.description()
        );
    }

    // Domain → ResponseDTO
    public LocationResponseDTO toResponse(Location domain) {
        if (domain == null) return null;

        return new LocationResponseDTO(
                domain.getId(),
                domain.getName(),
                domain.getAddress(),
                domain.getCapacity(),
                domain.getDescription()
        );
    }

    // Domain → Entity
    public LocationEntity toEntity(Location domain) {
        if (domain == null) return null;

        LocationEntity entity = new LocationEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setAddress(domain.getAddress());
        entity.setCapacity(domain.getCapacity());
        entity.setDescription(domain.getDescription());
        return entity;
    }

    // Entity → Domain
    public Location toDomain(LocationEntity entity) {
        if (entity == null) return null;

        return new Location(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getCapacity(),
                entity.getDescription()
        );
    }
}
