package ru.job4j.auth.service;

import org.springframework.stereotype.Service;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.domain.Role;
import ru.job4j.auth.repository.PersonRepository;
import ru.job4j.auth.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;

    public PersonService(PersonRepository personRepository, RoleRepository roleRepository) {
        this.personRepository = personRepository;
        this.roleRepository = roleRepository;
    }

    public List<Person> findAll() {
        return (List<Person>) personRepository.findAll();
    }

    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    public Person save(Person person) {
        if (person.getRole() == null) {
            person.setRole(roleRepository.findByName("ROLE_USER").get());
        }
        System.out.println("перед сохранением");
        return personRepository.save(person);
    }

    public void delete(Person person) {
        personRepository.delete(person);
    }
}
