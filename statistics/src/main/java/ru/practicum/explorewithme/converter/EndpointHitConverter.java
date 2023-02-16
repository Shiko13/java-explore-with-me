package ru.practicum.explorewithme.converter;

import ru.practicum.explorewithme.dto.EndpointHitDto;
import ru.practicum.explorewithme.model.EndpointHit;

public class EndpointHitConverter {

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        return new EndpointHit(null, endpointHitDto.getApp(), endpointHitDto.getUri(),
                endpointHitDto.getIp(), endpointHitDto.getTimestamp());
    }
}
