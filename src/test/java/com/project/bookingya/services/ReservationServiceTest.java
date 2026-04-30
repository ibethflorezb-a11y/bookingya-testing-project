package com.project.bookingya.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import com.project.bookingya.dtos.ReservationDto;
import com.project.bookingya.entities.ReservationEntity;
import com.project.bookingya.entities.RoomEntity;
import com.project.bookingya.exceptions.BusinessRuleException;
import com.project.bookingya.repositories.IGuestRepository;
import com.project.bookingya.repositories.IReservationRepository;
import com.project.bookingya.repositories.IRoomRepository;
import com.project.bookingya.shared.Constants;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private IReservationRepository reservationRepository;
    
    @Mock
    private IRoomRepository roomRepository;
    
    @Mock
    private IGuestRepository guestRepository;

    @Spy
    private ModelMapper mapper = new ModelMapper();

    @InjectMocks
    private ReservationService reservationService;

    private ReservationDto reservationDto;
    private ReservationEntity reservationEntity;
    private RoomEntity roomEntity;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reservationService, "reservationRepository", reservationRepository);
        ReflectionTestUtils.setField(reservationService, "roomRepository", roomRepository);
        ReflectionTestUtils.setField(reservationService, "guestRepository", guestRepository);
        ReflectionTestUtils.setField(reservationService, "mapper", mapper);

        UUID roomId = UUID.randomUUID();
        UUID guestId = UUID.randomUUID();

        reservationDto = new ReservationDto();
        reservationDto.setRoomId(roomId);
        reservationDto.setGuestId(guestId);
        reservationDto.setStartDate(LocalDateTime.now().plusDays(1));
        reservationDto.setEndDate(LocalDateTime.now().plusDays(3));
        reservationDto.setGuestsCount(2);

        reservationEntity = new ReservationEntity();
        reservationEntity.setId(UUID.randomUUID());
        reservationEntity.setRoomId(roomId);
        reservationEntity.setGuestId(guestId);
        reservationEntity.setStartDate(reservationDto.getStartDate());
        reservationEntity.setEndDate(reservationDto.getEndDate());
        reservationEntity.setGuestsCount(reservationDto.getGuestsCount());

        roomEntity = new RoomEntity();
        roomEntity.setId(roomId);
        roomEntity.setMaxGuests(4);
        roomEntity.setAvailable(true);
    }

    @Test
    void testCreateReservation_Success() {
        when(roomRepository.findById(any())).thenReturn(Optional.of(roomEntity));
        when(guestRepository.existsById(any())).thenReturn(true);
        when(reservationRepository.findConflictingReservations(any(), any(), any())).thenReturn(List.of());
        when(reservationRepository.saveAndFlush(any(ReservationEntity.class))).thenReturn(reservationEntity);

        ReservationDto result = reservationService.create(reservationDto);

        assertNotNull(result);
        assertEquals(reservationDto.getRoomId(), result.getRoomId());
    }

    @Test
    void testCreateReservation_InvalidDateRange() {
        reservationDto.setStartDate(LocalDateTime.now().plusDays(5));
        reservationDto.setEndDate(LocalDateTime.now().plusDays(2));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            reservationService.create(reservationDto);
        });

        assertEquals(Constants.INVALID_RESERVATION_RANGE, exception.getMessage());
    }

    @Test
    void testCreateReservation_RoomNotAvailable() {
        roomEntity.setAvailable(false);
        when(roomRepository.findById(any())).thenReturn(Optional.of(roomEntity));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            reservationService.create(reservationDto);
        });

        assertEquals(Constants.ROOM_NOT_AVAILABLE, exception.getMessage());
    }

    @Test
    void testCreateReservation_CapacityExceeded() {
        reservationDto.setGuestsCount(10);
        when(roomRepository.findById(any())).thenReturn(Optional.of(roomEntity));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            reservationService.create(reservationDto);
        });

        assertEquals(Constants.ROOM_CAPACITY_EXCEEDED, exception.getMessage());
    }

    @Test
    void testCreateReservation_OverlapDetected() {
        when(roomRepository.findById(any())).thenReturn(Optional.of(roomEntity));
        when(guestRepository.existsById(any())).thenReturn(true);
        when(reservationRepository.findConflictingReservations(any(), any(), any())).thenReturn(List.of(new ReservationEntity()));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            reservationService.create(reservationDto);
        });

        assertEquals(Constants.RESERVATION_OVERLAP_ROOM, exception.getMessage());
    }
}
