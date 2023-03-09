package ru.practicum.converter;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CommentCreateDto;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentConverter {
    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getId(),
                comment.getEvent().getId(),
                comment.getCreated(),
                comment.getUpdated(),
                comment.getVisible()
        );
    }

    public static Comment fromNewDto(CommentCreateDto dto, User author, Event event) {
        return new Comment(
                null,
                dto.getText(),
                author,
                event,
                LocalDateTime.now(),
                null,
                true
        );
    }
}
