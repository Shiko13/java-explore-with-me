package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.converter.EventConverter;
import ru.practicum.converter.LocationConverter;
import ru.practicum.dto.*;
import ru.practicum.exception.*;
import ru.practicum.model.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final StatsClient statsClient;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<EventShortDto> search(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, Boolean onlyAvailable, String sort,
                                      int from, int size, HttpServletRequest request) {

        EventFilter filter = updateFilter(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);

        String statsUri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        statsClient.createHit(request, statsUri);
        log.info("Hit from ip={} and uri={} during getting all events was sent to statistics.",
                ip, statsUri);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }

        List<Category> categoryEntities;
        if (categories != null) {
            categoryEntities = categoryRepository.findAllByIdIn(categories);
        } else {
            categoryEntities = categoryRepository.findAll();
        }

        List<Event> events;
        List<EventShortDto> eventList = new ArrayList<>();
        boolean sortEventDate = sort.equals(EventSort.EVENT_DATE.toString()) || sort.isBlank();
        boolean sortViews = sort.equals(EventSort.VIEWS.toString());
        Pageable pageable = PageRequest.of(from / size, size);

        if (sortEventDate) {
            if (text.isBlank()) {
                events = eventRepository.getAllEventsPublicByEventDateAllText(categoryEntities, paid,
                        rangeStart, rangeEnd, pageable);
            } else {
                events = eventRepository.getAllEventsPublicByEventDate(text, categoryEntities, paid,
                        rangeStart, rangeEnd, pageable);
            }
            List<Event> resultEvents = getEventsAvailableOrNot(events, onlyAvailable);
            eventList = getEventShortDtoList(resultEvents);
        }

        if (sortViews) {
            if (text.isBlank()) {
                events = eventRepository.getAllEventsPublicAllText(categoryEntities, paid,
                        rangeStart, rangeEnd, pageable);
            } else {
                events = eventRepository.getAllEventsPublic(text, categoryEntities, paid,
                        rangeStart, rangeEnd, pageable);
            }
            List<Event> resultEvents = getEventsAvailableOrNot(events, onlyAvailable);
            eventList = getEventShortDtoList(resultEvents);
            eventList.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        }
        log.info("Get all events={} after search text={}.", eventList, text);

        return eventList;
    }

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new NonUpdatedEventException("There is less than 2 hours before event", LocalDateTime.now());
        }

        User initiator = getUserFromRepository(userId);
        Category category = getCategoryFromRepository(eventDto.getCategory());
        Event event = EventConverter.fromDto(initiator, category, eventDto);
        Event savedEvent = eventRepository.save(event);
        EventFullDto savedEventFullDto = getEventFullDto(savedEvent);
        log.info("New event with id = {} from user with id {} created successfully.",
                savedEventFullDto.getId(), userId);

        return savedEventFullDto;
    }

    @Override
    @Transactional
    public EventFullDto updateByInitiator(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User initiator = getUserFromRepository(userId);
        Event event = getEventFromRepository(eventId);
        validateInitiator(initiator.getId(), event.getInitiator().getId(), event.getId());

        if (event.getState().equals(State.PUBLISHED)) {
            throw new NonUpdatedEventException(String.format("Event with status %s cannot be edit.",
                    State.PUBLISHED), LocalDateTime.now());
        }
        update(updateEventUserRequest, event);

        StateAction stateAction = updateEventUserRequest.getStateAction();

        if (stateAction != null) {
            switch (stateAction) {
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
            }
        }

        Event updatedEvent = eventRepository.save(event);
        log.info("Event with id={} canceled by initiator (id={}).", event.getId(), initiator.getId());

        return getEventFullDto(updatedEvent);
    }

    @Override
    public EventFullDto get(Long id, HttpServletRequest request) {
        String uri = request.getRequestURI();
        statsClient.createHit(request, uri);
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                String.format("There isn't event with id %d in this database.", id)
        ));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NonUpdatedEventException("Event is not published", LocalDateTime.now());
        }
        EventFullDto resultDto = getEventFullDto(event);
        log.info("Getting event with id={} with views = {} and confirmed requests = {}",
                resultDto.getId(), resultDto.getViews(), resultDto.getConfirmedRequests());

        return resultDto;
    }

    @Override
    public EventFullDto getByUser(Long userId, Long eventId) {
        final Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("There isn't event with id=%d in repository.", eventId)
                ));
        EventFullDto resultDto = getEventFullDto(event);
        log.info("Getting event with id={} by initiator with id={}",
                resultDto.getId(), resultDto.getInitiator().getId());

        return resultDto;
    }

    @Override
    public List<EventShortDto> getAllByUser(Long userId, Integer from, Integer size, HttpServletRequest httpServletRequest) {
        User initiator = getUserFromRepository(userId);
        List<Event> eventList = eventRepository.findEventsByInitiator_IdOrderById(
                initiator.getId(), PageRequest.of(from / size, size));
        List<EventShortDto> result = getEventShortDtoList(eventList);
        log.info("Get all events by request of user with id={}.", initiator.getId());

        return result;
    }

    @Override
    @Transactional
    public void deleteById(Long eventId) {
        eventRepository.deleteById(eventId);
        log.info("Event with id {} deleted successfully", eventId);
    }

    @Override
    @Transactional
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getEventDate() != null) {
            if (updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new DateException("There is less than 1 hour before event.", LocalDateTime.now());
            }
        }
        Event updatingEvent = getEventFromRepository(eventId);

        if (updateEventAdminRequest.getTitle() != null && !updateEventAdminRequest.getTitle().isBlank()) {
            updatingEvent.setTitle(updateEventAdminRequest.getTitle());
        }
        if (updateEventAdminRequest.getAnnotation() != null && !updateEventAdminRequest.getAnnotation().isBlank()) {
            updatingEvent.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getDescription() != null && !updateEventAdminRequest.getDescription().isBlank()) {
            updatingEvent.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category updatedCategory = getCategoryFromRepository(updateEventAdminRequest.getCategory());
            updatingEvent.setCategory(updatedCategory);
        }
        if (updateEventAdminRequest.getPaid() != null) {
            updatingEvent.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            updatingEvent.setLocation(LocationConverter.fromDto(updateEventAdminRequest.getLocation()));
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            updatingEvent.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            updatingEvent.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            updatingEvent.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        StateAction stateAction = updateEventAdminRequest.getStateAction();

        if (stateAction != null) {
            switch (stateAction) {
                case PUBLISH_EVENT:
                    if (updatingEvent.getState().equals(State.PENDING)) {
                        updatingEvent.setState(State.PUBLISHED);
                        updatingEvent.setPublishedOn(LocalDateTime.now());
                    } else
                        throw new NonUpdatedEventException("Cannot publish the event because " +
                                "it's not in the right state: " + stateAction, LocalDateTime.now());
                    break;
                case CANCEL_REVIEW:
                    if (!updatingEvent.getState().equals(State.PUBLISHED)) {
                        updatingEvent.setState(State.CANCELED);
                    } else
                        throw new NonUpdatedEventException("Cannot cancel the event because " +
                                "it's not in the right state: " + stateAction, LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    if (!updatingEvent.getState().equals(State.PUBLISHED)) {
                        updatingEvent.setState(State.CANCELED);
                    } else
                        throw new NonUpdatedEventException("Cannot reject the event because " +
                                "it's not in the right state: " + stateAction, LocalDateTime.now());
                    break;
                case SEND_TO_REVIEW:
                    updatingEvent.setState(State.PENDING);
                    break;
            }
        }

        Event updatedEvent = eventRepository.save(updatingEvent);
        log.info("Admin updated event with id={} successfully.", updatedEvent.getId());

        return getEventFullDto(updatedEvent);
    }

    @Override
    public List<EventFullDto> getAllByAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Category> categoryEntities;

        if (categories != null && !categories.isEmpty()) {
            categoryEntities = categoryRepository.findAllByIdIn(categories);
        } else {
            categoryEntities = categoryRepository.findAll();
        }

        List<User> userEntities;
        if (users != null && !users.isEmpty()) {
            userEntities = userRepository.findAllByIdIn(users);
        } else {
            userEntities = userRepository.findAll();
        }

        List<State> statesEnum = new ArrayList<>();
        if (states != null) {
            for (String state: states) {
                State status = State.valueOf(state);
                statesEnum.add(status);
            }
        } else {
            statesEnum.addAll(Arrays.asList(State.values()));
        }

        List<Event> events = eventRepository.getAllEventsByAdmin(userEntities, statesEnum,
                categoryEntities, rangeStart, rangeEnd, pageRequest);
        List<EventFullDto> eventList = getEventFullDtoList(events);
        log.info("Get all events list with length={} by admin request.", eventList.size());

        return eventList;
    }

    private User getUserFromRepository(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    throw new BadRequestException("User", userId, LocalDateTime.now());
                });
    }

    private Category getCategoryFromRepository(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    throw new BadRequestException("Category", categoryId, LocalDateTime.now());
                });
    }

    private Event getEventFromRepository(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    throw new BadRequestException("Event", eventId, LocalDateTime.now());
                });
    }

    private void validateInitiator(Long userId, Long eventInitiatorId, Long eventId) {
        if (!userId.equals(eventInitiatorId)) {
            throw new ValidationException(
                    String.format("User with id=%d is not the initiator of event with id=%d",
                            userId,
                            eventId)
            );
        }
    }

    private EventFullDto getEventFullDto(Event event) {
        Map<Long, Long> views = statsClient.getHits(Collections.singletonList(event.getId()));
        Integer confirmedRequests = requestRepository.findAllByEvent_IdAndStatus(event.getId(),
                Status.CONFIRMED).size();
        Map<Long, Integer> confirmedRequestsForDto = new HashMap<>(Map.of(event.getId(), confirmedRequests));

        return EventConverter.toFullDto(event, confirmedRequestsForDto, views);
    }

    private List<EventShortDto> getEventShortDtoList(List<Event> events) {
        if (events != null) {
            List<Long> eventIds = getEventIds(events);
            Map<Long, Long> views = statsClient.getHits(eventIds);
            Map<Long, Integer> eventsRequests = getEventRequests(eventIds);

            return EventConverter.toEventShortDtoList(events, eventsRequests, views);
        }
        return Collections.emptyList();
    }

    private List<EventFullDto> getEventFullDtoList(List<Event> events) {
        if (events != null) {
            List<Long> eventIds = getEventIds(events);
            Map<Long, Long> views = statsClient.getHits(eventIds);
            Map<Long, Integer> eventsRequests = getEventRequests(eventIds);
            return EventConverter.toFullDtoList(events, eventsRequests, views);
        }
        return Collections.emptyList();
    }

    private Map<Long, Integer> getEventRequests(List<Long> eventIds) {
        Map<Long, Integer> eventsRequests = new HashMap<>();
        List<ParticipationRequestShortDto> requestList = requestRepository.countAllByStatusAndEvent_IdsIn(
                Status.CONFIRMED, eventIds);
        for (int i = 0; i < requestList.size(); i++) {
            eventsRequests.put(requestList.get(i).getId(), requestList.get(i).getCount());
        }

        return eventsRequests;
    }

    private List<Event> getEventsAvailableOrNot(List<Event> events, boolean available) {
        List<Event> resultEvents = new ArrayList<>();
        if (available) {
            List<Event> filteredEvents = events.stream()
                    .filter(Event::getRequestModeration)
                    .collect(Collectors.toList());
            List<Long> eventIds = getEventIds(filteredEvents);
            List<ParticipationRequestShortDto> requestList = requestRepository.countAllByStatusAndEvent_IdsIn(
                    Status.CONFIRMED, eventIds);
            for (int i = 0; i < requestList.size(); i++) {
                if ((filteredEvents.get(i).getParticipantLimit() - requestList.get(i).getCount()) > 0) {
                    resultEvents.add(events.get(i));
                }
            }
            return resultEvents;
        }
        return events;
    }

    private List<Long> getEventIds(List<Event> events) {
        return events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
    }

    private void update(UpdateEventUserRequest eventRequest, Event event) {
        if (eventRequest.getEventDate() != null) {
            if (eventRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new DateException("There is less than 2 hours before event.", LocalDateTime.now());
            }
            event.setEventDate(eventRequest.getEventDate());
        }
        if (eventRequest.getTitle() != null && !eventRequest.getTitle().isBlank()) {
            event.setTitle(eventRequest.getTitle());
        }
        if (eventRequest.getAnnotation() != null && !eventRequest.getAnnotation().isBlank()) {
            event.setAnnotation(eventRequest.getAnnotation());
        }
        if (eventRequest.getDescription() != null && !eventRequest.getDescription().isBlank()) {
            event.setDescription(eventRequest.getDescription());
        }
        if (eventRequest.getCategory() != null) {
            Category category = getCategoryFromRepository(eventRequest.getCategory());
            event.setCategory(category);
        }
        if (eventRequest.getPaid() != null) {
            event.setPaid(eventRequest.getPaid());
        }

        event.setParticipantLimit(eventRequest.getParticipantLimit());
    }

    private EventFilter updateFilter(String text, List<Long> categories, Boolean paid,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                     Boolean onlyAvailable) {
        return EventFilter.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .build();
    }
}
