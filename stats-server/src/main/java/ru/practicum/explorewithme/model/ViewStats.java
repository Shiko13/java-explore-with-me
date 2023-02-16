package ru.practicum.explorewithme.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ViewStats {

    private String app;

    private String uri;

    private Long hits;
}
