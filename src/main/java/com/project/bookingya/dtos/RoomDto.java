package com.project.bookingya.dtos;
import java.util.UUID;
public class RoomDto {
    private UUID id;
    private String code;
    private String name;
    private String city;
    private Integer maxGuests;
    private Double nightlyPrice;
    private Boolean available;
    public RoomDto() {}
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
