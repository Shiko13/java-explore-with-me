package ru.practicum.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CommentCreateDto;
import ru.practicum.dto.CommentUpdateDto;
import ru.practicum.service.comment.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments/{commentId}")
    public CommentDto getById(
            @PathVariable @Positive Long eventId,
            @PathVariable @Positive Long commentId) {
        log.info("Getting comment with id={} to event with id={}", commentId, eventId);

        return commentService.getById(eventId, commentId);
    }

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getAllByEvent(
            @PathVariable @Positive Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Getting all comments to event with id={}", eventId);

        return commentService.getAllByEvent(eventId, from, size);
    }

    @GetMapping("/users/{userId}/comments")
    public List<CommentDto> getAllByUser(
            @PathVariable @Positive Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Receive get-request for getting all comments from user with id={}", userId);

        return commentService.getAllByUser(userId, from, size);
    }

    @PostMapping("/events/{eventId}/comments")
    public CommentDto create(@PathVariable @Positive Long eventId,
                                 @RequestBody @Valid CommentCreateDto comment) {
        log.info("Creating comment from user with id={}", comment.getAuthorId());

        return commentService.create(eventId, comment);
    }

    @PatchMapping("/events/{eventId}/comments")
    public CommentDto update(
            @PathVariable @Positive Long eventId,
            @RequestBody @Valid CommentUpdateDto comment) {
        log.info("Updating comment with id={} to event with id={}", comment.getId(), eventId);

        return commentService.update(eventId, comment);
    }

    @DeleteMapping("/users/{userId}/events/{eventId}/comments/{commentId}")
    public void deleteById(
            @PathVariable @Positive Long eventId,
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId) {
        log.info("Deleting comment with id={} from user with id={} to event with id={}",
                commentId, userId, eventId);

        commentService.deleteById(eventId, userId, commentId);
    }
}
