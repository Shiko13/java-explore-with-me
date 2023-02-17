package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.converter.EndpointHitConverter;
import ru.practicum.explorewithme.dto.EndpointHitDto;
import ru.practicum.explorewithme.model.App;
import ru.practicum.explorewithme.model.ViewStats;
import ru.practicum.explorewithme.repository.AppRepository;
import ru.practicum.explorewithme.repository.StatsRepository;

import org.springframework.transaction.annotation.Transactional;
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

        if (uris == null || uris.isEmpty()) {
            return null;
        }

        List<ViewStats> stats;
        if (unique) {
            stats = statsRepository.findAllUniqueIp(start, end, uris);
        } else {
            stats = statsRepository.findAll(start, end, uris);
        }

        return stats;
    }
}
