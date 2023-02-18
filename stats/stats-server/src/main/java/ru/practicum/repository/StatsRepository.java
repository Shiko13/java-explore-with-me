package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(name = "findAll", nativeQuery = true)
    List<ViewStats> findAll(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(name = "findAllUniqueIp", nativeQuery = true)
    List<ViewStats> findAllUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(name = "findAllIfNoUris", nativeQuery = true)
    List<ViewStats> findAllIfNoUris(LocalDateTime start, LocalDateTime end);

    @Query(name = "findAllIfNoUrisUnique", nativeQuery = true)
    List<ViewStats> findAllIfNoUrisUnique(LocalDateTime start, LocalDateTime end);
}
