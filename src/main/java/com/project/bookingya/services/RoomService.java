package com.project.bookingya.services;

import com.project.bookingya.dtos.RoomDto;
import com.project.bookingya.entities.RoomEntity;
import com.project.bookingya.exceptions.EntityNotExistsException;
import com.project.bookingya.repositories.IRoomRepository;
import com.project.bookingya.shared.Constants;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
public class RoomService {

    @Autowired
    private IRoomRepository roomRepository;

    @Autowired
    private ModelMapper mapper;

    public RoomDto create(RoomDto roomDto) {
        Objects.requireNonNull(roomDto, "RoomDto must not be null");
        RoomEntity entity = mapper.map(roomDto, RoomEntity.class);
        RoomEntity saved = roomRepository.saveAndFlush(entity);
        return mapper.map(saved, RoomDto.class);
    }

    public List<RoomDto> getAll() {
        return roomRepository.findAll().stream()
                .map(entity -> mapper.map(entity, RoomDto.class))
                .collect(Collectors.toList());
    }

    public RoomDto getById(UUID id) {
        Objects.requireNonNull(id, "ID must not be null");
        RoomEntity entity = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotExistsException(Constants.ROOM_NOT_FOUND));
        return mapper.map(entity, RoomDto.class);
    }

    public void delete(UUID id) {
        Objects.requireNonNull(id, "ID must not be null");
        if (!roomRepository.existsById(id)) {
            throw new EntityNotExistsException(Constants.ROOM_NOT_FOUND);
        }
        roomRepository.deleteById(id);
    }

    public RoomDto update(RoomDto roomDto, UUID id) {
        Objects.requireNonNull(roomDto, "RoomDto must not be null");
        Objects.requireNonNull(id, "ID must not be null");
        RoomEntity existing = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotExistsException(Constants.ROOM_NOT_FOUND));
        mapper.map(roomDto, existing);
        RoomEntity updated = roomRepository.saveAndFlush(existing);
        return mapper.map(updated, RoomDto.class);
    }
}
