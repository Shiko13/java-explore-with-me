package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {

    private Long id;
    @NotNull
    private String annotation;
    @NotNull
    private CategoryDto category;
    private Integer confirmedRequests;
    @NotNull
    private LocalDateTime eventDate;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private Boolean paid;
    @NotNull
    private String title;
    @NotNull
    private Long views;
}
