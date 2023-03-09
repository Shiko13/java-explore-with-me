package ru.practicum.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CommentDto;
import ru.practicum.service.comment.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping("/comments")
    public List<CommentDto> getAll(
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Getting all comments");

        return commentService.getAll(from, size);
    }

    @GetMapping("/comments/filter")
    public List<CommentDto> getAllByVisibility(
            @RequestParam(defaultValue = "false") Boolean visible,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Getting all comments with visibility={}", visible);

        return commentService.getAllByVisibility(visible, from, size);
    }

    @PatchMapping("/events/{eventId}/comments/{commentId}/show")
    public CommentDto show(@PathVariable @Positive Long eventId,
                                  @PathVariable @Positive Long commentId) {
        log.info("Showing comment with id={} to event with id={}",
                commentId, eventId);

        return commentService.show(eventId, commentId);
    }

    @PatchMapping("/events/{eventId}/comments/{commentId}/hide")
    public CommentDto hide(
            @PathVariable @Positive Long eventId,
            @PathVariable @Positive Long commentId) {
        log.info("Hiding comment with id={} to event with id={}",
                commentId, eventId);

        return commentService.hide(eventId, commentId);
    }

    @DeleteMapping("/events/{eventId}/comments/{commentId}")
    public void deleteByAdmin(
            @PathVariable @Positive Long eventId,
            @PathVariable @Positive Long commentId) {
        log.info("Deleting comment with id={} to event with id={}",
                commentId, eventId);

        commentService.deleteByAdmin(eventId, commentId);
    }
}
