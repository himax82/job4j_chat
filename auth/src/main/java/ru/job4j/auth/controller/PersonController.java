package ru.job4j.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.service.PatchService;
import ru.job4j.auth.service.PersonService;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService service;
    private final BCryptPasswordEncoder encoder;

    public PersonController(PersonService service, BCryptPasswordEncoder encoder) {
        this.service = service;
        this.encoder = encoder;
    }

    @GetMapping({"/", "/all"})
    public List<Person> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        Person person = service.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Person with id " + id + " is not found."
        ));
        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Person> register(@Valid @RequestBody Person person) {
        Objects.requireNonNull(person.getUsername(), "Name mustn't be empty");
        Objects.requireNonNull(person.getPassword(), "Password mustn't be empty");

        if (person.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password length must be more than 5 characters.");
        }

        return new ResponseEntity<>(service.save(person), HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid @RequestBody Person person) {
        Objects.requireNonNull(person.getUsername(), "Name mustn't be empty");
        Objects.requireNonNull(person.getPassword(), "Password mustn't be empty");

        if (person.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password length must be more than 5 characters.");
        }

        service.findById(person.getId()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Person with id " + person.getId() + " is not found."
        ));

        service.save(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = service.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person with id " + id + " is not found."
                ));
        service.delete(person);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/")
    public ResponseEntity<Void> patch(@RequestBody Person person)
            throws InvocationTargetException, IllegalAccessException {
        Person existingPerson = service.findById(person.getId()).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person with id " + person.getId() + " is not found."
                ));

        if (person.getPassword() != null) {
            person.setPassword(encoder.encode(person.getPassword()));
        }

        Person patch = (Person) new PatchService<>().getPatch(existingPerson, person);
        service.save(patch);
        return ResponseEntity.ok().build();
    }
}