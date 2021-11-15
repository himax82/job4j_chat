package ru.job4j.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.domain.Message;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.domain.Room;
import ru.job4j.auth.service.MessageService;
import ru.job4j.auth.service.PersonService;
import ru.job4j.auth.service.RoomService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/message")
public class MessageController {
    private final MessageService service;
    private final RoomService roomService;
    private final PersonService personService;

    public MessageController(MessageService service, RoomService roomService, PersonService personService) {
        this.service = service;
        this.roomService = roomService;
        this.personService = personService;
    }

    @GetMapping("/")
    public List<Message> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> findById(@PathVariable int id) {
        Message message = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Message with id " + id + " is not found."
                ));
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(message.getText().length())
                .body(message.getText());
    }

    @PostMapping("/")
    public ResponseEntity<Message> create(@RequestBody Message message) {
        Objects.requireNonNull(message.getText(), "Text mustn't be empty");
        Objects.requireNonNull(message.getRoom(), "Room mustn't be empty");
        Objects.requireNonNull(message.getPerson(), "Person mustn't be empty");

        Person person = personService.findById(message.getPerson().getId()).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Person with id " + message.getPerson().getId() + " is not found."
                ));
        Room room = roomService.findById(message.getRoom().getId()).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room with id " + message.getRoom().getId() + " is not found."
                ));
        message.setPerson(person);
        message.setRoom(room);
        message.setCreated(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(message);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Message message) {
        Objects.requireNonNull(message.getText(), "Text mustn't be empty");
        Objects.requireNonNull(message.getRoom(), "Room mustn't be empty");
        Objects.requireNonNull(message.getPerson(), "Person mustn't be empty");

        Message existingMessage = service.findById(message.getId()).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Message with id " + message.getId() + " is not found."
                ));
        Person person = personService.findById(message.getPerson().getId()).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Person with id " + message.getPerson().getId() + " is not found."
                ));
        Room room = roomService.findById(message.getRoom().getId()).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room with id " + message.getRoom().getId() + " is not found."
                ));
        message.setPerson(person);
        message.setRoom(room);
        message.setCreated(existingMessage.getCreated());

        service.save(message);
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
}