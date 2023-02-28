package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.EventRequestStatusUpdateRequest;
import ru.practicum.model.EventRequestStatusUpdateResult;
import ru.practicum.model.State;
import ru.practicum.model.Status;
import ru.practicum.converter.RequestConverter;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exception.*;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getAll(Long userId) {
        log.info("Getting all requests from repository");
        User user = getUserFromRepository(userId);

        return requestRepository.findAllByRequester_Id(user.getId()).stream()
                .map(RequestConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getForEvent(Long userId, Long eventId) {
        log.info("Getting request for event");
        User user = getUserFromRepository(userId);
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, user.getId()).orElseThrow(
                () -> new EntityNotFoundException(String.format("There isn't event with id=%d in repository.", eventId))
        );

        return requestRepository.findAllByEvent_Id(event.getId()).stream()
                .map(RequestConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {
        log.info("Creating request");
        User requester = getUserFromRepository(userId);
        Event event = getEventFromRepository(eventId);

        if (requestRepository.findByEvent_IdAndRequester_Id(event.getId(), requester.getId()).isPresent()) {
            throw new ForbiddenException("Participation request for event with this id already exists.", LocalDateTime.now());
        }

        if (event.getInitiator().getId().equals(requester.getId())) {
            throw new ValidateConflictException(String.format("User with id=%d cannot request for his own event (id=%d)",
                    requester.getId(),
                    event.getId()), LocalDateTime.now()
            );
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ValidateConflictException(
                    "User cannot add request for unpublished event", LocalDateTime.now()
            );
        }

        Integer confirmedParticipationRequests = requestRepository.findAllByEvent_IdAndStatus(event.getId(),
                Status.CONFIRMED).size();
        if (event.getParticipantLimit().equals(confirmedParticipationRequests)) {
            throw new ValidateConflictException("There isn't more places in this event", LocalDateTime.now());
        }

        Status status = Status.PENDING;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            status = Status.CONFIRMED;
        }

        ParticipationRequest requestToAdd = new ParticipationRequest(null,
                requester,
                event,
                status,
                LocalDateTime.now());
        ParticipationRequest savedRequest = requestRepository.save(requestToAdd);
        log.info("Participation request with id={} saved successfully.", savedRequest.getId());

        return RequestConverter.toDto(savedRequest);
    }

    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        log.info("Canceling request");
        User user = getUserFromRepository(userId);
        ParticipationRequest request = getRequestFromRepository(requestId);

        if (!user.getId().equals(request.getRequester().getId())) {
            throw new ForbiddenException("User with this id haven't access to cancel event with this id",
                    LocalDateTime.now());
        }

        request.setStatus(Status.CANCELED);
        ParticipationRequest savedRequest = requestRepository.save(request);
        log.info("Request with id={} to event with id={} successfully canceled by user with id={}.",
                request.getId(), request.getEvent().getId(), user.getId());

        return RequestConverter.toDto(savedRequest);
    }

    @Override
    public EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest request) {
        Event event = getEventFromRepository(eventId);
        List<Long> requestIds = request.getRequestIds();
        Status status = request.getStatus();

        Integer confirmedParticipationRequests = requestRepository.findAllByEvent_IdAndStatus(event.getId(),
                Status.CONFIRMED).size();
        if (event.getParticipantLimit().equals(confirmedParticipationRequests)) {
            throw new ValidateConflictException("There isn't more places in this event", LocalDateTime.now());
        }

        Integer participantLimit = event.getParticipantLimit();
        int availableParticipants = participantLimit - confirmedParticipationRequests;
        int potentialParticipants = requestIds.size();

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        if (participantLimit != 0 && availableParticipants <= 0) {
            throw new ParticipantLimitException("The participant limit = " + participantLimit +
                    " has been reached");
        }

        List<ParticipationRequest> requests = requestIds.stream()
                .map(this::getRequestFromRepository)
                .map(this::checkStatus)
                .collect(Collectors.toList());

        if (participantLimit == 0 || !event.getRequestModeration()) {
            confirmedRequests = requests.stream()
                    .peek(r -> r.setStatus(Status.CONFIRMED))
                    .map(requestRepository::save)
                    .map(RequestConverter::toDto)
                    .collect(Collectors.toList());
            return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        }

        if (status.equals(Status.REJECTED)) {
            rejectedRequests = requests.stream()
                    .peek(r -> r.setStatus(Status.REJECTED))
                    .map(requestRepository::save)
                    .map(RequestConverter::toDto)
                    .collect(Collectors.toList());
            return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        }

        if (status.equals(Status.CONFIRMED)) {
            if (potentialParticipants <= availableParticipants) {
                confirmedRequests = requests.stream()
                        .peek(r -> r.setStatus(Status.CONFIRMED))
                        .map(requestRepository::save)
                        .map(RequestConverter::toDto)
                        .collect(Collectors.toList());
            } else {
                confirmedRequests = requests.stream()
                        .limit(availableParticipants)
                        .peek(r -> r.setStatus(Status.CONFIRMED))
                        .map(requestRepository::save)
                        .map(RequestConverter::toDto)
                        .collect(Collectors.toList());
                rejectedRequests = requests.stream()
                        .skip(availableParticipants)
                        .peek(r -> r.setStatus(Status.REJECTED))
                        .map(requestRepository::save)
                        .map(RequestConverter::toDto)
                        .collect(Collectors.toList());
            }
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private User getUserFromRepository(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    throw new BadRequestException("User", id, LocalDateTime.now());
                });
    }

    private Event getEventFromRepository(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> {
                    throw new BadRequestException("Event", id, LocalDateTime.now());
                });
    }

    private ParticipationRequest getRequestFromRepository(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> {
                    throw new BadRequestException("Request", id, LocalDateTime.now());
                });
    }

    private ParticipationRequest checkStatus(ParticipationRequest request) {
        if (!request.getStatus().equals(Status.PENDING)) {
            throw new NotPendingStatusException("Request must have status PENDING");
        }
        return request;
    }
}
