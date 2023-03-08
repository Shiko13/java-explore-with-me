package ru.practicum.converter;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;

@UtilityClass
public class RequestConverter {

    public ParticipationRequestDto toDto(ParticipationRequest request) {
        return new ParticipationRequestDto(request.getId(), request.getCreated(),
                request.getEvent().getId(), request.getRequester().getId(),
                 request.getStatus());
    }
}
