package ru.practicum.service.comment;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CommentCreateDto;
import ru.practicum.dto.CommentUpdateDto;

import java.util.List;

public interface CommentService {
    CommentDto create(Long eventId, CommentCreateDto dto);

    CommentDto getById(Long eventId, Long commentId);

    List<CommentDto> getAll(int from, int size);

    List<CommentDto> getAllByEvent(Long eventId, int from, int size);

    List<CommentDto> getAllByUser(Long userId, int from, int size);

    List<CommentDto> getAllByVisibility(boolean visible, int from, int size);

    CommentDto update(Long eventId, CommentUpdateDto request);

    CommentDto hide(Long eventId, Long commentId);

    CommentDto show(Long eventId, Long commentId);

    void deleteById(Long eventId, Long userId, Long commentId);

    void deleteByAdmin(Long eventId, Long commentId);
}
