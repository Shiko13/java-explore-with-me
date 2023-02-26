package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class LocationDto {
    @NotBlank
    private Double lat;
    @NotBlank
    private Double lon;
}
