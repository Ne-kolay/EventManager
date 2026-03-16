package com.example.event.manager.event.mapper;

import com.example.event.manager.event.domain.Event;
import com.example.event.manager.event.dto.EventCreateRequestDTO;
import com.example.event.manager.event.dto.EventDTO;
import com.example.event.manager.event.dto.EventUpdateRequestDTO;
import com.example.event.manager.event.entity.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    // CreateRequestDTO → Domain
    public Event toDomain(EventCreateRequestDTO dto) {
        if (dto == null) return null;

        return new Event(
                null,
                dto.name(),
                null,
                dto.maxPlaces(),
                0, //при создании, занятых мест точно 0
                dto.date(),
                dto.cost(),
                dto.duration(),
                dto.locationId(),
                null
        );
    }

    // UpdateRequestDTO → Domain
    public Event toDomain(EventUpdateRequestDTO dto) {
        if (dto == null) return null;

        return new Event(
                null,
                dto.name(),
                null,
                dto.maxPlaces(),
                null,
                dto.date(),
                dto.cost(),
                dto.duration(),
                dto.locationId(),
                null
        );
    }

    // Entity → Domain
    public Event toDomain(EventEntity entity) {
        if (entity == null) return null;

        return new Event(
                entity.getId(),
                entity.getName(),
                entity.getOwnerId(),
                entity.getMaxPlaces(),
                entity.getOccupiedPlaces(),
                entity.getDate(),
                entity.getCost(),
                entity.getDuration(),
                entity.getLocationId(),
                entity.getStatus()
        );
    }

    // Domain → Entity
    public EventEntity toEntity(Event domain) {
        if (domain == null) return null;

        EventEntity entity = new EventEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setOwnerId(domain.getOwnerId());
        entity.setMaxPlaces(domain.getMaxPlaces());
        entity.setOccupiedPlaces(domain.getOccupiedPlaces() != null ? domain.getOccupiedPlaces() : 0);
        entity.setDate(domain.getDate());
        entity.setCost(domain.getCost());
        entity.setDuration(domain.getDuration());
        entity.setLocationId(domain.getLocationId());
        entity.setStatus(domain.getStatus());
        return entity;
    }

    // Domain → DTO
    public EventDTO toDto(Event domain) {
        if (domain == null) return null;

        return new EventDTO(
                domain.getId(),
                domain.getName(),
                domain.getOwnerId(),
                domain.getMaxPlaces(),
                domain.getOccupiedPlaces(),
                domain.getDate(),
                domain.getCost(),
                domain.getDuration(),
                domain.getLocationId(),
                domain.getStatus()
        );
    }
}