package com.project.bookingya.services;

import com.project.bookingya.dtos.GuestDto;
import com.project.bookingya.entities.GuestEntity;
import com.project.bookingya.exceptions.EntityNotExistsException;
import com.project.bookingya.repositories.IGuestRepository;
import com.project.bookingya.shared.Constants;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
public class GuestService {

    @Autowired
    private IGuestRepository guestRepository;

    @Autowired
    private ModelMapper mapper;

    public GuestDto create(GuestDto guestDto) {
        Objects.requireNonNull(guestDto, "GuestDto must not be null");
        GuestEntity entity = mapper.map(guestDto, GuestEntity.class);
        GuestEntity saved = guestRepository.saveAndFlush(entity);
        return mapper.map(saved, GuestDto.class);
    }

    public List<GuestDto> getAll() {
        return guestRepository.findAll().stream()
                .map(entity -> mapper.map(entity, GuestDto.class))
                .collect(Collectors.toList());
    }

    public GuestDto getById(UUID id) {
        Objects.requireNonNull(id, "ID must not be null");
        GuestEntity entity = guestRepository.findById(id)
                .orElseThrow(() -> new EntityNotExistsException(Constants.GUEST_NOT_FOUND));
        return mapper.map(entity, GuestDto.class);
    }

    public void delete(UUID id) {
        Objects.requireNonNull(id, "ID must not be null");
        if (!guestRepository.existsById(id)) {
            throw new EntityNotExistsException(Constants.GUEST_NOT_FOUND);
        }
        guestRepository.deleteById(id);
    }

    public GuestDto update(GuestDto guestDto, UUID id) {
        Objects.requireNonNull(guestDto, "GuestDto must not be null");
        Objects.requireNonNull(id, "ID must not be null");
        GuestEntity existing = guestRepository.findById(id)
                .orElseThrow(() -> new EntityNotExistsException(Constants.GUEST_NOT_FOUND));
        mapper.map(guestDto, existing);
        GuestEntity updated = guestRepository.saveAndFlush(existing);
        return mapper.map(updated, GuestDto.class);
    }
}
