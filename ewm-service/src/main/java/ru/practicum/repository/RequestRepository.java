package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Status;
import ru.practicum.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequester_Id(Long requesterId);

    List<ParticipationRequest> findAllByEvent_Id(Long eventId);

    Optional<ParticipationRequest> findByEvent_IdAndRequester_Id(Long eventId, Long userId);

    @Query("select pr from ParticipationRequest as pr " +
            "where pr.event.id = ?1 " +
            "and pr.status = ?2")
    List<ParticipationRequest> findAllByEvent_IdAndStatus(Long eventId, Status state);

    @Query("select count(pr) from ParticipationRequest pr " +
            "where pr.status = ?1 " +
            "and pr.event.id in ?2")
    List<Integer> countAllByStatusAndEvent_IdsIn(Status state, List<Long> eventIds);
}
