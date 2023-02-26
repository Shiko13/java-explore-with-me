package ru.practicum.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.UpdateCompilationRequest;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.service.compilation.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto create(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Post /admin/compilations with newCompilationDto={}", newCompilationDto);
        return compilationService.create(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    public void delete(@NotNull @PathVariable Long compId) {
        log.info("Delete /admin/compilations/{}", compId);
        compilationService.delete(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable Long compId, @Valid @RequestBody UpdateCompilationRequest request) {
        log.info("Patch /admin/compilations/{} with request={}", compId, request);
        return compilationService.update(compId, request);
    }
}
