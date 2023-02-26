package ru.practicum.converter;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.LocationDto;
import ru.practicum.model.Location;

@UtilityClass
public class LocationConverter {
    public static Location fromDto(LocationDto dto) {
        return new Location(
                dto.getLat(),
                dto.getLon()
        );
    }

    public static LocationDto toDto(Location location) {
        return new LocationDto(
                location.getLat(),
                location.getLon()
        );
    }
}
