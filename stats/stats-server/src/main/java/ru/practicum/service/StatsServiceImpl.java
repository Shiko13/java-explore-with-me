package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.converter.EndpointHitConverter;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.App;
import ru.practicum.repository.AppRepository;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final AppRepository appRepository;

    @Override
    @Transactional
    public void create(EndpointHitDto endpointHitDto) {
        log.info("Start method create in StatsServiceImpl with endpointHitDto={}", endpointHitDto);
        App app = appRepository.findByName(endpointHitDto.getApp())
                        .orElseGet(() -> appRepository.save(new App(null, endpointHitDto.getApp())));
        statsRepository.save(EndpointHitConverter.toEndpointHit(endpointHitDto, app));
    }

    @Override
    public List<ViewStats> getAll(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Start method getAll in StatsServiceImpl with start={}, end={}, " +
                "uris={}, unique={}", start, end, uris, unique);

        List<ViewStats> stats;

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                stats = statsRepository.findAllIfNoUrisUnique(start, end);
            } else {
                stats = statsRepository.findAllIfNoUris(start, end);
            }
        } else {
            if (unique) {
                stats = statsRepository.findAllUniqueIp(start, end, uris);
            } else {
                stats = statsRepository.findAll(start, end, uris);
            }
        }

        return stats;
    }
}
