package ru.practicum.service.compilation;

import ru.practicum.UpdateCompilationRequest;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    CompilationDto getById(Long compId);

    CompilationDto create(NewCompilationDto newCompilationDto);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest request);
}
