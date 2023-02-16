package ru.practicum.explorewithme.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Data
@Component
public class EndpointHitDto {

    private String app;

    private String uri;

    private String ip;

    private LocalDateTime timestamp;
}
