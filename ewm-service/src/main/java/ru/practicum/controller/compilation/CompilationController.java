package ru.practicum.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.service.compilation.CompilationService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam Boolean pinned, @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get /compilations with pinned={}, from={}, size={}", pinned, from, size);
        return compilationService.getAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@NotNull @PathVariable Long compId) {
        log.info("Get /compilations/{}", compId);
        return compilationService.getById(compId);
    }
}
