package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndEvent_Id(Long commentId, Long eventId);

    Optional<Comment> findByIdAndEvent_IdAndAuthor_Id(Long commentId, Long eventId, Long authorId);

    List<Comment> findByEvent_IdOrderByCreatedDesc(Long eventId, Pageable pageable);

    List<Comment> findByAuthor_IdOrderByCreatedDesc(Long authorId, Pageable pageable);

    List<Comment> findByVisibleIsOrderByCreatedDesc(boolean visible, Pageable pageable);

    @Query("select c from Comment as c " +
            "order by c.created desc")
    List<Comment> findAllComments(Pageable pageable);
}
