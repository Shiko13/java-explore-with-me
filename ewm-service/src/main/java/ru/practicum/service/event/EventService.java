package ru.practicum.service.event;

import ru.practicum.model.UpdateEventAdminRequest;
import ru.practicum.model.UpdateEventUserRequest;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> search(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd, Boolean onlyAvailable, String sort,
                                     int from, int size, HttpServletRequest request);

    EventFullDto create(Long userId, NewEventDto eventDto);

    EventFullDto update(Long userId, UpdateEventUserRequest eventRequest);

    EventFullDto updateByInitiator(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto get(Long id, HttpServletRequest request);

    EventFullDto getByUser(Long userId, Long eventId);

    List<EventShortDto> getAllByUser(Long userId, Integer from, Integer size, HttpServletRequest httpServletRequest);

    void deleteById(Long eventId);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventFullDto> getAllByAdmin(List<Long> users, List<String> states,
                                     List<Long> categories, LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd, int from, int size);
}


