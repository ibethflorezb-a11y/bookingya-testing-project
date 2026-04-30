package com.project.bookingya.dtos;
import java.time.LocalDateTime;
import java.util.UUID;
public class ReservationDto {
    private UUID id;
    private UUID roomId;
    private UUID guestId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer guestsCount;
    public ReservationDto() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID roomId) { this.roomId = roomId; }
    public UUID getGuestId() { return guestId; }
    public void setGuestId(UUID guestId) { this.guestId = guestId; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public Integer getGuestsCount() { return guestsCount; }
    public void setGuestsCount(Integer guestsCount) { this.guestsCount = guestsCount; }
}
