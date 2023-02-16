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
@NamedNativeQueries({
        @NamedNativeQuery(name = "findAll", resultSetMapping = "mapperFromEndpointHitToViewStats",
                query = "select app, uri, count(ip) as hits from endpoint_hits " +
                        "where timestamp between ?1 and ?2 and uri in ?3 group by app, uri order by hits desc"),
        @NamedNativeQuery(name = "findAllUniqueIp", resultSetMapping = "mapperFromEndpointHitToViewStats",
                query = "select app, uri, count(distinct ip) as hits from endpoint_hits " +
                        "where timestamp between ?1 and ?2 and uri in ?3 group by app, uri order by hits desc")})
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
