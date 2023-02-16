package ru.practicum.explorewithme.model;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Component
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "endpoint_hits")
@SqlResultSetMapping(name = "mapperFromEndpointHitToViewStats",
        classes = @ConstructorResult(
                targetClass = ViewStats.class,
                columns = {@ColumnResult(name = "app", type = String.class),
                        @ColumnResult(name = "uri", type = String.class),
                        @ColumnResult(name = "hits", type = Long.class)}))
public class EndpointHit {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app")
    private String app;

    @Column(name = "uri")
    private String uri;

    @Column(name = "ip")
    private String ip;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
