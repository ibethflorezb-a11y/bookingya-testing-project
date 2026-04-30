package com.project.bookingya.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.project.bookingya.dtos.ReservationDto;
import com.project.bookingya.entities.ReservationEntity;
import com.project.bookingya.entities.RoomEntity;
import com.project.bookingya.exceptions.BusinessRuleException;
import com.project.bookingya.repositories.IGuestRepository;
import com.project.bookingya.repositories.IReservationRepository;
import com.project.bookingya.repositories.IRoomRepository;
import com.project.bookingya.services.ReservationService;
import com.project.bookingya.shared.Constants;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.annotations.Steps;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

public class ReservationSteps {

    @Steps
    ReservationService reservationService;

    private IReservationRepository reservationRepository;
    private IRoomRepository roomRepository;
    private IGuestRepository guestRepository;
    private ModelMapper mapper;

    ReservationDto reservationDto;
    ReservationDto createdReservation;
    UUID reservationId;
    Exception lastException;

    @Before
    public void setup() {
        reservationRepository = mock(IReservationRepository.class);
        roomRepository = mock(IRoomRepository.class);
        guestRepository = mock(IGuestRepository.class);
        mapper = new ModelMapper();
        
        reservationService = new ReservationService();
        ReflectionTestUtils.setField(reservationService, "reservationRepository", reservationRepository);
        ReflectionTestUtils.setField(reservationService, "roomRepository", roomRepository);
        ReflectionTestUtils.setField(reservationService, "guestRepository", guestRepository);
        ReflectionTestUtils.setField(reservationService, "mapper", mapper);
    }

    @Given("a room is available for booking")
    public void a_room_is_available_for_booking() {
        RoomEntity room = new RoomEntity();
        room.setAvailable(true);
        room.setMaxGuests(10);
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));
        when(roomRepository.existsById(any())).thenReturn(true);
    }

    @Given("a guest exists")
    public void a_guest_exists() {
        when(guestRepository.existsById(any())).thenReturn(true);
    }

    @When("the user creates a reservation with valid details")
    public void the_user_creates_a_reservation_with_valid_details() {
        reservationDto = new ReservationDto();
        reservationDto.setRoomId(UUID.randomUUID());
        reservationDto.setGuestId(UUID.randomUUID());
        reservationDto.setStartDate(LocalDateTime.now().plusDays(1));
        reservationDto.setEndDate(LocalDateTime.now().plusDays(2));
        reservationDto.setGuestsCount(2);
        
        ReservationEntity entity = new ReservationEntity();
        entity.setId(UUID.randomUUID());
        entity.setGuestsCount(2);
        
        when(reservationRepository.findConflictingReservations(any(), any(), any())).thenReturn(List.of());
        when(reservationRepository.saveAndFlush(any())).thenReturn(entity);
        
        createdReservation = reservationService.create(reservationDto);
    }

    @Then("the reservation should be successfully created")
    public void the_reservation_should_be_successfully_created() {
        assertNotNull(createdReservation);
    }

    @Given("a reservation exists with a specific ID")
    public void a_reservation_exists_with_a_specific_id() {
        reservationId = UUID.randomUUID();
        ReservationEntity entity = new ReservationEntity();
        entity.setId(reservationId);
        
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(entity));
        a_room_is_available_for_booking();
        a_guest_exists();
    }

    @When("the user requests the reservation by that ID")
    public void the_user_requests_the_reservation_by_that_id() {
        createdReservation = reservationService.getById(reservationId);
    }

    @Then("the reservation details should be returned")
    public void the_reservation_details_should_be_returned() {
        assertNotNull(createdReservation);
    }

    @Given("an existing reservation")
    public void an_existing_reservation() {
        a_reservation_exists_with_a_specific_id();
    }

    @When("the user updates the reservation with new details")
    public void the_user_updates_the_reservation_with_new_details() {
        reservationDto = new ReservationDto();
        reservationDto.setGuestsCount(3);
        
        ReservationEntity entity = new ReservationEntity();
        entity.setId(reservationId);
        entity.setGuestsCount(3);
        
        when(reservationRepository.saveAndFlush(any())).thenReturn(entity);
        
        createdReservation = reservationService.update(reservationDto, reservationId);
    }

    @Then("the reservation should be successfully updated")
    public void the_reservation_should_be_successfully_updated() {
        assertNotNull(createdReservation);
        assertEquals(3, createdReservation.getGuestsCount());
    }

    @When("the user cancels the reservation")
    public void the_user_cancels_the_reservation() {
        when(reservationRepository.existsById(reservationId)).thenReturn(true);
        reservationService.delete(reservationId);
    }

    @Then("the reservation should be successfully deleted")
    public void the_reservation_should_be_successfully_deleted() {
        // Verification logic
    }

    @Given("the room is already reserved for those dates")
    public void the_room_is_already_reserved_for_those_dates() {
        a_room_is_available_for_booking();
        a_guest_exists();
        when(reservationRepository.findConflictingReservations(any(), any(), any())).thenReturn(List.of(new ReservationEntity()));
    }

    @When("I attempt to create a reservation for the same room and dates")
    public void i_attempt_to_create_a_reservation_for_the_same_room_and_dates() {
        reservationDto = new ReservationDto();
        reservationDto.setRoomId(UUID.randomUUID());
        reservationDto.setGuestId(UUID.randomUUID());
        reservationDto.setStartDate(LocalDateTime.now().plusDays(1));
        reservationDto.setEndDate(LocalDateTime.now().plusDays(2));
        reservationDto.setGuestsCount(2);
        
        lastException = assertThrows(BusinessRuleException.class, () -> {
            reservationService.create(reservationDto);
        });
    }

    @Then("the reservation should be rejected with an overlap error")
    public void the_reservation_should_be_rejected_with_an_overlap_error() {
        assertEquals(Constants.RESERVATION_OVERLAP_ROOM, lastException.getMessage());
    }

    @Given("a room with capacity {int}")
    public void a_room_with_capacity(Integer capacity) {
        RoomEntity room = new RoomEntity();
        room.setAvailable(true);
        room.setMaxGuests(capacity);
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));
        when(roomRepository.existsById(any())).thenReturn(true);
        a_guest_exists();
    }

    @When("I attempt to create a reservation for {int} guests")
    public void i_attempt_to_create_a_reservation_for_guests(Integer guests) {
        reservationDto = new ReservationDto();
        reservationDto.setRoomId(UUID.randomUUID());
        reservationDto.setGuestId(UUID.randomUUID());
        reservationDto.setStartDate(LocalDateTime.now().plusDays(1));
        reservationDto.setEndDate(LocalDateTime.now().plusDays(2));
        reservationDto.setGuestsCount(guests);
        
        lastException = assertThrows(BusinessRuleException.class, () -> {
            reservationService.create(reservationDto);
        });
    }

    @Then("the reservation should be rejected with a capacity error")
    public void the_reservation_should_be_rejected_with_a_capacity_error() {
        assertEquals(Constants.ROOM_CAPACITY_EXCEEDED, lastException.getMessage());
    }
}
