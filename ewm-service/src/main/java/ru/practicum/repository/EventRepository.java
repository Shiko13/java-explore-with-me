package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    List<Event> findEventsByInitiator_IdOrderById(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiator_Id(Long eventId, Long userId);

    List<Event> findAllByCategory_Id(Long categoryId);
}
