package com.example.event.manager.event.entity;

import com.example.event.manager.event.status.EventStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private Integer maxPlaces;

    @Column(nullable = false)
    private Integer occupiedPlaces = 0;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private BigDecimal cost;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Long locationId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "event",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<RegistrationEntity> registrations = new ArrayList<>();

    public EventEntity() { }
    public EventEntity(Long id,
                       String name,
                       Long ownerId,
                       Integer maxPlaces,
                       LocalDateTime date,
                       BigDecimal cost,
                       Integer duration,
                       Long locationId,
                       EventStatus status) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.maxPlaces = maxPlaces;
        this.date = date;
        this.cost = cost;
        this.duration = duration;
        this.locationId = locationId;
        this.status = status;
    }

    //GETTERS & SETTERS
    public List<RegistrationEntity> getRegistrations() { return registrations; }
    public void setRegistrations(List<RegistrationEntity> registrations) { this.registrations = registrations; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public Integer getOccupiedPlaces() { return occupiedPlaces; }
    public void setOccupiedPlaces(Integer occupiedPlaces) { this.occupiedPlaces = occupiedPlaces; }

    public Integer getMaxPlaces() { return maxPlaces; }
    public void setMaxPlaces(Integer maxPlaces) { this.maxPlaces = maxPlaces; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
