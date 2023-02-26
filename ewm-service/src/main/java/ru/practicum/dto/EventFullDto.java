package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.Category;
import ru.practicum.model.Location;
import ru.practicum.State;
import ru.practicum.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    @NotNull
    private String annotation;
    @NotNull
    private Category category;
    private Integer confirmedRequests;
    private String description;
    private LocalDateTime eventDate;
    private Long id;
    @NotNull
    private User initiator;
    @NotNull
    private Location location;
    @NotNull
    private Boolean paid;
    private Integer participantLimit;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private State state;
    @NotNull
    private String title;
    private Long views;
}