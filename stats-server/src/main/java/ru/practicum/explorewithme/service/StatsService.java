package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.EndpointHitDto;
import ru.practicum.explorewithme.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void create(EndpointHitDto endpointHitDto);

    List<ViewStats> getAll(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
