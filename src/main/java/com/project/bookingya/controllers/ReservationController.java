package com.project.bookingya.controllers;
import com.project.bookingya.dtos.ReservationDto;
import com.project.bookingya.services.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation Controller", description = "API for managing reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;
    @GetMapping
    @Operation(summary = "Get all reservations")
    public ResponseEntity<List<ReservationDto>> getAll() {
        return ResponseEntity.ok(reservationService.getAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID")
    public ResponseEntity<ReservationDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }
    @PostMapping
    @Operation(summary = "Create a new reservation")
    public ResponseEntity<ReservationDto> create(@RequestBody ReservationDto reservationDto) {
        return ResponseEntity.ok(reservationService.create(reservationDto));
    }
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing reservation")
    public ResponseEntity<ReservationDto> update(@RequestBody ReservationDto reservationDto, @PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.update(reservationDto, id));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reservation")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        reservationService.delete(id);
        return ResponseEntity.ok().build();
    }
}
