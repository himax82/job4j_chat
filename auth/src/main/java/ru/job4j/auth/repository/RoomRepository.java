package ru.job4j.auth.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.job4j.auth.domain.Room;

import java.util.Optional;

public interface RoomRepository extends CrudRepository<Room, Integer> {
    @Override
    @Query("select r from Room r left join fetch r.messages")
    Iterable<Room> findAll();

    @Override
    @Query("select r from Room r left join fetch r.messages where r.id = :paramId")
    Optional<Room> findById(@Param("paramId") Integer integer);
}
