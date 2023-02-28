package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.State;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findEventsByInitiator_IdOrderById(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiator_Id(Long eventId, Long userId);

    @Query("select e " +
            "from Event e " +
            "where e.category in ?1 " +
            "and e.paid = ?2 " +
            "and e.eventDate between ?3 and ?4 " +
            "group by e.id " +
            "order by e.eventDate desc")
    List<Event> getAllEventsPublicByEventDateAllText(List<Category> categoryEntities, Boolean paid,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     Pageable pageable);

    @Query("select e " +
            "from Event e " +
            "where e.category in ?1 " +
            "and e.paid = ?2 " +
            "and e.eventDate between ?3 and ?4 " +
            "group by e.id " +
            "order by e.id desc")
    List<Event> getAllEventsPublicAllText(List<Category> categoryEntities, Boolean paid,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                          Pageable pageable);

    @Query("select e " +
            "from Event e " +
            "where upper(e.annotation) like upper(concat('%',?1,'%')) " +
            "or upper(e.description) like upper(concat('%',?1,'%')) " +
            "and e.category in ?2 " +
            "and e.paid = ?3 " +
            "and e.eventDate between ?4 and ?5 " +
            "group by e.id " +
            "order by e.eventDate desc")
    List<Event> getAllEventsPublicByEventDate(String text, List<Category> categoryEntities, Boolean paid,
                                              LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                              Pageable pageable);

    @Query("select e " +
            "from Event as e " +
            "where upper(e.annotation) like upper(concat('%',?1,'%')) " +
            "or upper(e.description) like upper(concat('%',?1,'%')) " +
            "and e.category in ?2 " +
            "and e.paid = ?3 " +
            "and e.eventDate between ?4 and ?5 " +
            "group by e.id " +
            "order by e.id")
    List<Event> getAllEventsPublic(String text, List<Category> categoryEntities, Boolean paid,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                   Pageable pageable);

    @Query("select e " +
            "from Event e " +
            "where e.initiator in ?1 " +
            "and e.state in ?2 " +
            "and e.category in ?3 " +
            "and e.eventDate between ?4 and ?5 " +
            "group by e.id " +
            "order by e.eventDate desc")
    List<Event> getAllEventsByAdmin(List<User> users, List<State> states, List<Category> categoryEntities,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                    PageRequest pageRequest);

    List<Event> findAllByCategory_Id(Long categoryId);
}
