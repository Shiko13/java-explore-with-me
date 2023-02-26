package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EventRequestStatusUpdateRequest;
import ru.practicum.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.request.RequestService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getAll(@NotNull @PathVariable Long userId) {
        log.info("Get /users/{}/requests", userId);
        return requestService.getAll(userId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getForEvent(@NotNull @PathVariable Long userId,
                                                @NotNull @PathVariable Long eventId) {
        log.info("Get /users/{}/events/{eventId}/requests", userId, eventId);
        return requestService.getForEvent(eventId, userId);
    }

    @PostMapping("/requests")
    public ParticipationRequestDto create(@NotNull @PathVariable Long userId,
                                                @NotNull @RequestParam Long eventId) {
        log.info("Post /users/{}/requests with eventId={}", userId, eventId);
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancel(@NotNull @PathVariable Long userId,
                                                @NotNull @PathVariable Long requestId) {
        log.info("Patch /users/{}/requests/{}/cancel", userId, requestId);
        return requestService.cancel(userId, requestId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatus(@NotNull @PathVariable Long userId,
                                                       @NotNull @PathVariable Long eventId,
                                                       @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Patch /users/{}/events/{}/requests", userId, eventId);
        return requestService.updateStatus(userId, eventId, request);
    }
}