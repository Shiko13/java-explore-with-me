package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.EndpointHitDto;
import ru.practicum.explorewithme.model.ViewStats;
import ru.practicum.explorewithme.service.StatsServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsServiceImpl statsService;

    @GetMapping("/stats")
    public List<ViewStats> getAll(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") Boolean unique) {

        return statsService.getAll(start, end, uris, unique);
    }

    @PostMapping("/hit")
    public void create(@RequestBody EndpointHitDto endpointHitDto) {
        statsService.create(endpointHitDto);
    }
}
