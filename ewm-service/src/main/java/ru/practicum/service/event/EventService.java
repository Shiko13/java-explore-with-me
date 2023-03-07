package ru.practicum.service.event;

import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.model.AdminEventParameters;
import ru.practicum.model.EventParameters;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventShortDto> search(EventParameters parameters, Boolean onlyAvailable, String sort,
                               int from, int size, HttpServletRequest request);

    EventFullDto create(Long userId, NewEventDto eventDto);


    EventFullDto updateByInitiator(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto get(Long id, HttpServletRequest request);

    EventFullDto getByUser(Long userId, Long eventId);

    List<EventShortDto> getAllByUser(Long userId, Integer from, Integer size, HttpServletRequest httpServletRequest);

    void deleteById(Long eventId);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventFullDto> getAllByAdmin(AdminEventParameters parameters, int from, int size);
}


