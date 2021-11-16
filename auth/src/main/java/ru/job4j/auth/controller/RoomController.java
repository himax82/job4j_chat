package ru.job4j.auth.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.domain.Room;
import ru.job4j.auth.dto.RoomDto;
import ru.job4j.auth.service.PatchService;
import ru.job4j.auth.service.RoomService;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/room")
public class RoomController {
    private final RoomService service;
    private final ModelMapper modelMapper;

    public RoomController(RoomService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/")
    public List<RoomDto> findAll() {
        return service.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> findById(@PathVariable("id") int id) {
        Room room = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room with id " + id + " is not found."
                ));

        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Room> create(@Valid @RequestBody Room room) {
        Objects.requireNonNull(room.getName(), "Name mustn't be empty");

        return new ResponseEntity<>(service.save(room), HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid @RequestBody Room room) {
        Objects.requireNonNull(room.getName(), "Name mustn't be empty");

        service.findById(room.getId()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Room with id " + room.getId() + " is not found."
        ));
        service.save(room);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        Room room = new Room();
        room.setId(id);
        service.delete(room);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/")
    public ResponseEntity<Void> patch(@RequestBody Room room)
            throws InvocationTargetException, IllegalAccessException {
        Room existingRoom = service.findById(room.getId()).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room with id " + room.getId() + " is not found."
                ));

        Room patch = (Room) new PatchService<>().getPatch(existingRoom, room);
        service.save(patch);
        return ResponseEntity.ok().build();
    }

    private RoomDto convertToDto(Room room) {
        return modelMapper.map(room, RoomDto.class);
    }
}
