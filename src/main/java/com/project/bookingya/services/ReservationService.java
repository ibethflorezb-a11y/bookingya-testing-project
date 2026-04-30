package com.project.bookingya.services;

import com.project.bookingya.dtos.ReservationDto;
import com.project.bookingya.entities.ReservationEntity;
import com.project.bookingya.exceptions.EntityNotExistsException;
import com.project.bookingya.repositories.IGuestRepository;
import com.project.bookingya.repositories.IReservationRepository;
import com.project.bookingya.repositories.IRoomRepository;
import com.project.bookingya.shared.Constants;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
public class ReservationService {

    @Autowired
    private IReservationRepository reservationRepository;
    
    @Autowired
    private IRoomRepository roomRepository;
    
    @Autowired
    private IGuestRepository guestRepository;
    
    @Autowired
    private ModelMapper mapper;

    public ReservationService() {
    }

    private void configureMapper() {
        if (mapper != null) {
            mapper.getConfiguration()
                  .setMatchingStrategy(MatchingStrategies.STRICT)
                  .setAmbiguityIgnored(true);
        }
    }

    public ReservationDto create(ReservationDto reservationDto) {
        Objects.requireNonNull(reservationDto, "ReservationDto must not be null");
        
        if (reservationDto.getStartDate() != null && reservationDto.getEndDate() != null && 
            !reservationDto.getStartDate().isBefore(reservationDto.getEndDate())) {
            throw new com.project.bookingya.exceptions.BusinessRuleException(Constants.INVALID_RESERVATION_RANGE);
        }

        if (reservationDto.getGuestsCount() != null && reservationDto.getGuestsCount() <= 0) {
            throw new com.project.bookingya.exceptions.BusinessRuleException(Constants.INVALID_GUESTS_COUNT);
        }

        if (roomRepository != null) {
            com.project.bookingya.entities.RoomEntity room = roomRepository.findById(reservationDto.getRoomId())
                .orElseThrow(() -> new EntityNotExistsException(Constants.ROOM_NOT_FOUND));
            
            if (Boolean.FALSE.equals(room.getAvailable())) {
                throw new com.project.bookingya.exceptions.BusinessRuleException(Constants.ROOM_NOT_AVAILABLE);
            }

            if (reservationDto.getGuestsCount() != null && reservationDto.getGuestsCount() > room.getMaxGuests()) {
                throw new com.project.bookingya.exceptions.BusinessRuleException(Constants.ROOM_CAPACITY_EXCEEDED);
            }
        }

        if (guestRepository != null && !guestRepository.existsById(reservationDto.getGuestId())) {
            throw new EntityNotExistsException(Constants.GUEST_NOT_FOUND);
        }

        if (reservationRepository != null) {
            if (!reservationRepository.findConflictingReservations(reservationDto.getRoomId(), 
                reservationDto.getStartDate(), reservationDto.getEndDate()).isEmpty()) {
                throw new com.project.bookingya.exceptions.BusinessRuleException(Constants.RESERVATION_OVERLAP_ROOM);
            }
        }
        
        configureMapper();
        
        ReservationEntity entity = mapper.map(reservationDto, ReservationEntity.class);
        if (reservationRepository != null) {
            ReservationEntity saved = reservationRepository.saveAndFlush(entity);
            return mapper.map(saved, ReservationDto.class);
        }
        return reservationDto;
    }

    public List<ReservationDto> getAll() {
        if (reservationRepository == null) return List.of();
        return reservationRepository.findAll().stream()
                .map(entity -> mapper.map(entity, ReservationDto.class))
                .collect(Collectors.toList());
    }

    public ReservationDto getById(UUID id) {
        Objects.requireNonNull(id, "ID must not be null");
        if (reservationRepository == null) return new ReservationDto();
        ReservationEntity entity = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotExistsException(Constants.RESERVATION_NOT_FOUND));
        return mapper.map(entity, ReservationDto.class);
    }

    public void delete(UUID id) {
        Objects.requireNonNull(id, "ID must not be null");
        if (reservationRepository != null) {
            if (!reservationRepository.existsById(id)) {
                throw new EntityNotExistsException(Constants.RESERVATION_NOT_FOUND);
            }
            reservationRepository.deleteById(id);
        }
    }

    public List<ReservationDto> getByRoomId(UUID roomId) {
        Objects.requireNonNull(roomId, "RoomID must not be null");
        if (reservationRepository == null) return List.of();
        return reservationRepository.findByRoomId(roomId).stream()
                .map(entity -> mapper.map(entity, ReservationDto.class))
                .collect(Collectors.toList());
    }

    public List<ReservationDto> getByGuestId(UUID guestId) {
        Objects.requireNonNull(guestId, "GuestID must not be null");
        if (reservationRepository == null) return List.of();
        return reservationRepository.findByGuestId(guestId).stream()
                .map(entity -> mapper.map(entity, ReservationDto.class))
                .collect(Collectors.toList());
    }

    public boolean isRoomAvailable(UUID roomId, LocalDateTime startDate, LocalDateTime endDate) {
        Objects.requireNonNull(roomId, "RoomID must not be null");
        if (reservationRepository == null) return true;
        return reservationRepository.findConflictingReservations(roomId, startDate, endDate).isEmpty();
    }

    public ReservationDto update(ReservationDto reservationDto, UUID id) {
        Objects.requireNonNull(reservationDto, "ReservationDto must not be null");
        Objects.requireNonNull(id, "ID must not be null");
        if (reservationRepository == null) return reservationDto;
        ReservationEntity existing = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotExistsException(Constants.RESERVATION_NOT_FOUND));
        
        configureMapper();
        mapper.map(reservationDto, existing);
        ReservationEntity updated = reservationRepository.saveAndFlush(existing);
        return mapper.map(updated, ReservationDto.class);
    }
}
