package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.converter.EndpointHitConverter;
import ru.practicum.explorewithme.dto.EndpointHitDto;
import ru.practicum.explorewithme.model.ViewStats;
import ru.practicum.explorewithme.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void create(EndpointHitDto endpointHitDto) {
        log.info("Start method create in StatsServiceImpl with endpointHitDto={}", endpointHitDto);
        statsRepository.save(EndpointHitConverter.toEndpointHit(endpointHitDto));
    }

    @Override
    public List<ViewStats> getAll(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Start method getAll in StatsServiceImpl with start={}, end={}, " +
                "uris={}, unique={}", start, end, uris, unique);
        List<ViewStats> stats;
        if (unique) {
            stats = statsRepository.findAllUniqueIp(start, end, uris);
        } else {
            stats = statsRepository.findAll(start, end, uris);
        }
        return stats;
    }
}
