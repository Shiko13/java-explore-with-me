package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventParameters {

    private List<Long> users;

    private List<String> states;

    private List<Long> categories;

    private LocalDateTime rangeStart;

    private LocalDateTime rangeEnd;
}
