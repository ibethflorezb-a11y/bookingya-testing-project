package com.project.bookingya.entities;

import java.util.UUID;
import jakarta.persistence.*;

@Entity
@Table(name = "room")
public class RoomEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String code;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private Integer maxGuests;
    @Column(nullable = false)
    private Double nightlyPrice;
    @Column(nullable = false)
    private Boolean available;

    public RoomEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Integer getMaxGuests() { return maxGuests; }
    public void setMaxGuests(Integer maxGuests) { this.maxGuests = maxGuests; }
    public Double getNightlyPrice() { return nightlyPrice; }
    public void setNightlyPrice(Double nightlyPrice) { this.nightlyPrice = nightlyPrice; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
}
