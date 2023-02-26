package ru.practicum.converter;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.List;

@UtilityClass
public class CompilationConverter {
    public static CompilationDto toDto(Compilation compilation, List<EventShortDto> events) {
        return new CompilationDto(compilation.getId(), compilation.getPinned(),
                compilation.getTitle(), events);
    }

    public Compilation fromDto(NewCompilationDto newCompilationDto, List<Event> events) {
        return new Compilation(null, newCompilationDto.getPinned(),
                newCompilationDto.getTitle(), events);
    }
}
