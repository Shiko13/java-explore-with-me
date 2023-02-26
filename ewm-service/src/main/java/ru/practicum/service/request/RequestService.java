package ru.practicum.service.request;

import ru.practicum.EventRequestStatusUpdateRequest;
import ru.practicum.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getAll(Long userId);

    List<ParticipationRequestDto> getForEvent(Long userId, Long eventId);

    ParticipationRequestDto create(Long userId, Long eventId);

    ParticipationRequestDto cancel(Long userId, Long requestId);

    EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request);
}
