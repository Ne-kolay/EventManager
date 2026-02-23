package com.example.Event.manager.location.mapper;

import com.example.Event.manager.location.dto.LocationDTO;
import com.example.Event.manager.location.domain.Location;
import com.example.Event.manager.location.entity.LocationEntity;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    // DTO → Domain
    public Location toDomain(LocationDTO dto) {
        if (dto == null) return null;

        return new Location(
                dto.id(),
                dto.name(),
                dto.address(),
                dto.capacity(),
                dto.description()
        );
    }

    // Domain → DTO
    public LocationDTO toDto(Location domain) {
        if (domain == null) return null;

        return new LocationDTO(
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

