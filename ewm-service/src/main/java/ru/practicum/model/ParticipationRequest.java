package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
public class ParticipationRequest {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "created")
    private LocalDateTime created;
}
