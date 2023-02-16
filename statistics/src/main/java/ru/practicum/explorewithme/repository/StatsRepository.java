package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.model.EndpointHit;
import ru.practicum.explorewithme.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "select e.app, e.uri, count(e.ip) from EndpointHit as e " +
            "where e.timestamp >= ?1 and e.timestamp <= ?2 and e.ip in ?3")
    List<ViewStats> findAll(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select e.app, e.uri, count(distinct e.ip) from EndpointHit as e " +
            "where e.timestamp >= ?1 and e.timestamp <= ?2 and e.ip in ?3")
    List<ViewStats> findAllUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
