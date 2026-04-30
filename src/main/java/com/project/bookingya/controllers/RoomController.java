package com.project.bookingya.controllers;

import com.project.bookingya.dtos.RoomDto;
import com.project.bookingya.services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Room Controller", description = "API for managing rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    @Operation(summary = "Get all rooms")
    public ResponseEntity<List<RoomDto>> getAll() {
        return ResponseEntity.ok(roomService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID")
    public ResponseEntity<RoomDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(roomService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new room")
    public ResponseEntity<RoomDto> create(@RequestBody RoomDto roomDto) {
        return ResponseEntity.ok(roomService.create(roomDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing room")
    public ResponseEntity<RoomDto> update(@RequestBody RoomDto roomDto, @PathVariable UUID id) {
        return ResponseEntity.ok(roomService.update(roomDto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a room")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
