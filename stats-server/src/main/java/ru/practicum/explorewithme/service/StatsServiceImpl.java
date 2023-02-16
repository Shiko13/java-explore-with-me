package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.converter.EndpointHitConverter;
import ru.practicum.explorewithme.dto.EndpointHitDto;
import ru.practicum.explorewithme.model.ViewStats;
import ru.practicum.explorewithme.repository.StatsRepository;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final static String LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Override
    public void create(EndpointHitDto endpointHitDto) {

        statsRepository.save(EndpointHitConverter.toEndpointHit(endpointHitDto));
    }

    @Override
    public List<ViewStats> getAll(String start, String end, List<String> uris, Boolean unique) {
        String decodedStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
        LocalDateTime startTime = LocalDateTime.parse(decodedStart,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String decodedEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);
        LocalDateTime endTime = LocalDateTime.parse(decodedEnd,
                DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_PATTERN));
        List<ViewStats> stats;
        if (unique) {
            stats = statsRepository.findAllUniqueIp(startTime, endTime, uris);
        } else {
            stats = statsRepository.findAll(startTime, endTime, uris);
        }
        return stats;
    }
}
