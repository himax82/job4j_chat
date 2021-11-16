package ru.job4j.auth.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.domain.Message;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.domain.Room;
import ru.job4j.auth.dto.MessageDto;
import ru.job4j.auth.service.MessageService;
import ru.job4j.auth.service.PatchService;
import ru.job4j.auth.service.PersonService;
import ru.job4j.auth.service.RoomService;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/message")
public class MessageController {
    private final MessageService service;
    private final RoomService roomService;
    private final PersonService personService;
    private final ModelMapper modelMapper;

    public MessageController(MessageService service, RoomService roomService,
                             PersonService personService, ModelMapper modelMapper) {
        this.service = service;
        this.roomService = roomService;
        this.personService = personService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/")
    public List<MessageDto> findAll() {
        return service.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> findById(@PathVariable int id) {
        Message message = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Message with id " + id + " is not found."
                ));
        return ResponseEntity.status(HttpStatus.OK)
                .body(convertToDto(message));
    }

    @PostMapping("/")
    public ResponseEntity<MessageDto> create(@Valid @RequestBody MessageDto messageDto) {
        Objects.requireNonNull(messageDto.getText(), "Text mustn't be empty");
        if (messageDto.getPersonId() == 0) {
            throw new NullPointerException("Person's id mustn't be 0");
        }
        if (messageDto.getRoomId() == 0) {
            throw new NullPointerException("Room's id mustn't be 0");
        }
        Message message = convertToEntity(messageDto);
        message.setCreated(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(convertToDto(service.save(message)));
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid @RequestBody MessageDto messageDto) {
        Objects.requireNonNull(messageDto.getText(), "Text mustn't be empty");
        if (messageDto.getPersonId() == 0) {
            throw new NullPointerException("Person's id mustn't be 0");
        }
        if (messageDto.getRoomId() == 0) {
            throw new NullPointerException("Room's id mustn't be 0");
        }
        Message existingMessage = service.findById(messageDto.getId()).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Message with id " + messageDto.getId() + " is not found."
                ));
        Message message = convertToEntity(messageDto);
        message.setCreated(existingMessage.getCreated());
        service.save(message);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/")
    public ResponseEntity<Void> patch(@RequestBody MessageDto messageDto)
            throws InvocationTargetException, IllegalAccessException {
        Message existingMessage = service.findById(messageDto.getId()).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Message with id " + messageDto.getId() + " is not found."
                ));
        Message message = convertToEntity(messageDto);
        Message patch = (Message) new PatchService<>().getPatch(existingMessage, message);
        service.save(patch);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Message message = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Message with id " + id + " is not found."
                ));
        service.delete(message);
        return ResponseEntity.ok().build();
    }

    private MessageDto convertToDto(Message message) {
        return modelMapper.map(message, MessageDto.class);
    }

    private Message convertToEntity(MessageDto messageDto) {
        Message message = modelMapper.map(messageDto, Message.class);

        if (messageDto.getPersonId() == 0) {
            message.setPerson(null);
        } else {
            Person person = personService.findById(messageDto.getPersonId()).orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Person with id " + messageDto.getPersonId() + " is not found."
                    ));
            message.setPerson(person);
        }

        if (messageDto.getRoomId() == 0) {
            message.setRoom(null);
        } else {
            Room room = roomService.findById(messageDto.getRoomId()).orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Room with id " + messageDto.getRoomId() + " is not found."
                    ));
            message.setRoom(room);
        }

        return message;
    }
}