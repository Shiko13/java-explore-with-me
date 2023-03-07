package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.EventParameters;
import ru.practicum.service.event.EventService;
import ru.practicum.dto.UpdateEventUserRequest;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/events")
    public List<EventShortDto> search(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(defaultValue = "false") Boolean paid,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest request) {

        EventParameters parameters = EventParameters.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();

        return eventService.search(parameters, onlyAvailable, sort, from, size, request);

//        return eventService.search(
//                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @PostMapping("/users/{userId}/events")
    public ResponseEntity<EventFullDto> create(@PathVariable @Positive Long userId,
                                               @RequestBody @Valid NewEventDto eventDto) {
        log.info("Get request for saving event {} from user with id={}", eventDto.getTitle(), userId);
        return new ResponseEntity<>(eventService.create(userId, eventDto), HttpStatus.CREATED);
    }


    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto update(@PathVariable @Positive Long userId,
                               @PathVariable @Positive Long eventId,
                               @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Get request for updating event with id={} by initiator (id={}).", eventId, userId);
        return eventService.updateByInitiator(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/events/{id}")
    public EventFullDto get(@PathVariable @Positive Long id, HttpServletRequest request) {
        log.info("Get request for getting event with id={}.", id);
        return eventService.get(id, request);
    }

    @GetMapping("users/{userId}/events/{eventId}")
    public EventFullDto getById(@PathVariable @Positive Long userId,
                                @PathVariable @Positive Long eventId) {
        log.info("Get request for getting event with id={} from user with id={}.", eventId, userId);
        return eventService.getByUser(userId, eventId);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getAllByUser(
            @PathVariable @Positive Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpServletRequest) {
        log.info("Get request for getting all events added by user with id={}", userId);
        return eventService.getAllByUser(userId, from, size, httpServletRequest);
    }

    @DeleteMapping("/events/{eventId}")
    public void deleteById(@PathVariable Long eventId) {
        eventService.deleteById(eventId);
        log.info("Get request for deleting event with id={}.", eventId);
    }
}
