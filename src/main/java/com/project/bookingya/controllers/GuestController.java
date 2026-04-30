package com.project.bookingya.controllers;

import com.project.bookingya.dtos.GuestDto;
import com.project.bookingya.services.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/guests")
@Tag(name = "Guest Controller", description = "API for managing guests")
public class GuestController {

    @Autowired
    private GuestService guestService;

    @GetMapping
    @Operation(summary = "Get all guests")
    public ResponseEntity<List<GuestDto>> getAll() {
        return ResponseEntity.ok(guestService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get guest by ID")
    public ResponseEntity<GuestDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(guestService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new guest")
    public ResponseEntity<GuestDto> create(@RequestBody GuestDto guestDto) {
        return ResponseEntity.ok(guestService.create(guestDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing guest")
    public ResponseEntity<GuestDto> update(@RequestBody GuestDto guestDto, @PathVariable UUID id) {
        return ResponseEntity.ok(guestService.update(guestDto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a guest")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        guestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
