package ru.practicum.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.converter.CommentConverter;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.CommentCreateDto;
import ru.practicum.dto.CommentUpdateDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto create(Long eventId, CommentCreateDto commentDto) {
        User author = getUserFromRepository(commentDto.getAuthorId());
        Event event = getEventFromRepository(eventId);
        Comment comment = commentRepository.save(CommentConverter.fromNewDto(commentDto, author, event));
        log.info("Comment with id={} from user with id={} to event with id={} saved successfully",
                comment.getId(), event.getId(), author.getId());

        return CommentConverter.toDto(comment);
    }

    @Override
    public CommentDto getById(Long eventId, Long commentId) {
        Comment comment = findCommentByIdAndEvent(commentId, eventId);
        log.info("Comment with id={} to event with id={} found successfully", commentId, eventId);

        return CommentConverter.toDto(comment);
    }

    @Override
    public List<CommentDto> getAll(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return commentRepository.findAllComments(pageRequest).stream()
                .map(CommentConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllByEvent(Long eventId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return commentRepository.findByEvent_IdOrderByCreatedDesc(eventId, pageRequest).stream()
                .map(CommentConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllByUser(Long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return commentRepository.findByAuthor_IdOrderByCreatedDesc(userId, pageRequest).stream()
                .map(CommentConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllByVisibility(boolean visible, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return commentRepository.findByVisibleIsOrderByCreatedDesc(visible, pageRequest).stream()
                .map(CommentConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto update(Long eventId, CommentUpdateDto request) {
        Comment comment = commentRepository.findByIdAndEvent_IdAndAuthor_Id(
                        request.getId(), eventId, request.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Comment with id=%d by user with id=%d to event with id=%d don't exist",
                        request.getId(), request.getAuthorId(), eventId)
                ));
        comment.setText(request.getText());
        comment.setUpdated(LocalDateTime.now());
        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment with id={} to event with id ={} successfully updated by text={}",
                updatedComment.getId(), updatedComment.getEvent().getId(), updatedComment.getText());

        return CommentConverter.toDto(updatedComment);
    }

    @Override
    @Transactional
    public CommentDto hide(Long eventId, Long commentId) {
        Comment comment = findCommentByIdAndEvent(commentId, eventId);
        if (comment.getVisible().equals(false)) {
            throw new BadRequestException("Comment with id=%d already hidden", comment.getId(), LocalDateTime.now());
        }
        comment.setVisible(false);
        Comment updatedComment = commentRepository.save(comment);
        log.info("Admin hide comment with id={} to event with id={}",
                updatedComment.getId(), updatedComment.getEvent().getId());

        return CommentConverter.toDto(updatedComment);
    }

    @Override
    @Transactional
    public CommentDto show(Long eventId, Long commentId) {
        Comment comment = findCommentByIdAndEvent(commentId, eventId);
        if (comment.getVisible().equals(true)) {
            throw new BadRequestException("Comment with id=%d already visible", comment.getId(), LocalDateTime.now());
        }
        comment.setVisible(true);
        Comment updatedComment = commentRepository.save(comment);
        log.info("Admin show comment with id={} to event with id={}",
                updatedComment.getId(), updatedComment.getEvent().getId());

        return CommentConverter.toDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteById(Long eventId, Long userId, Long commentId) {
        commentRepository.findByIdAndEvent_IdAndAuthor_Id(commentId, eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "There isn't comment with id=%d from user with id=%d to event with id=%d",
                        commentId, userId, eventId)));
        commentRepository.deleteById(commentId);
        log.info("Comment with id={} from event with id={} successfully deleted",
                commentId, eventId);
    }

    @Override
    public void deleteByAdmin(Long eventId, Long commentId) {
        findCommentByIdAndEvent(commentId, eventId);
        commentRepository.deleteById(commentId);
        log.info("Comment with id={} from event with id={} successfully deleted by admin",
                commentId, eventId);
    }

    private User getUserFromRepository(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new BadRequestException("There isn't user with id %d in this database",
                        id, LocalDateTime.now()));
    }

    private Event getEventFromRepository(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() ->
                        new BadRequestException("There isn't event with id=%d in repository",
                                id, LocalDateTime.now()));
    }

    private Comment findCommentByIdAndEvent(Long commentId, Long eventId) {
        return commentRepository.findByIdAndEvent_Id(commentId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "There isn't comment with id=%d from event with id=%d in this database",
                        commentId, eventId
                )));
    }
}
