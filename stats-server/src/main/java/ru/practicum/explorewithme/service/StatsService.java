package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.EndpointHitDto;
import ru.practicum.explorewithme.model.ViewStats;

import java.util.List;

public interface StatsService {
    void create(EndpointHitDto endpointHitDto);

    List<ViewStats> getAll(String start, String end, List<String> uris, Boolean unique);
}
