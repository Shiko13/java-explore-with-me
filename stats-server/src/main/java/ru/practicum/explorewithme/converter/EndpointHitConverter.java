package ru.practicum.explorewithme.converter;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.EndpointHitDto;
import ru.practicum.explorewithme.model.App;
import ru.practicum.explorewithme.model.EndpointHit;

@UtilityClass
public class EndpointHitConverter {

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto, App app) {
        return new EndpointHit(null, app, endpointHitDto.getUri(),
                endpointHitDto.getIp(), endpointHitDto.getTimestamp());
    }
}
