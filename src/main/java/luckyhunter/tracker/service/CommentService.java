package luckyhunter.tracker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import luckyhunter.tracker.mapper.CommentMapper;
import luckyhunter.tracker.model.dto.CommentDto;
import luckyhunter.tracker.model.entity.Comment;
import luckyhunter.tracker.model.entity.Task;
import luckyhunter.tracker.repository.CommentRepository;
import luckyhunter.tracker.validator.CommentValidator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Collections;
import java.util.List;

public class CommentService {
    private final SessionFactory sessionFactory;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentService(SessionFactory sessionFactory, CommentRepository commentRepository, CommentMapper commentMapper) {
        this.sessionFactory = sessionFactory;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public List<CommentDto> getCommentsByTaskId(Long taskId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            List<CommentDto> result = commentRepository.getCommentsByTaskId(session, taskId)
                    .stream()
                    .map(commentMapper::toCommentDto)
                    .toList();

            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public CommentDto getCommentByCommentId(Long commentId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                CommentDto commentDto = commentMapper.toCommentDto(commentRepository.getCommentByCommentId(session, commentId));
                session.getTransaction().commit();
                return commentDto;
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        }
    }

    public CommentDto createComment(CommentDto commentDto, Long taskId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                Comment comment = commentMapper.toComment(commentDto);
                Task task = session.get(Task.class, taskId);
                if (task == null) {
                    throw new IllegalArgumentException("Task not found with id: " + taskId);
                }
                comment.setTask(task);
                commentRepository.createComment(session, comment);
                session.getTransaction().commit();
                return commentMapper.toCommentDto(comment);
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        }
    }

    public CommentDto updateComment(CommentDto updatedCommentDto, Long commentId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                Comment comment = commentRepository.getCommentByCommentId(session, commentId);
                Comment updatedComment = commentMapper.toComment(updatedCommentDto);

                comment.setTitle(updatedComment.getTitle());
                comment.setDescription(updatedComment.getDescription());

                commentRepository.updateComment(session, comment);

                session.getTransaction().commit();
                return commentMapper.toCommentDto(comment);
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        }
    }

    public boolean deleteComment(int commentId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                boolean deleted = commentRepository.deleteComment(session, commentId);
                session.getTransaction().commit();
                return deleted;
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        }
    }

    public CommentValidator parseToCommentDto(String body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(body);
        CommentValidator commentValidator = new CommentValidator();

        String title = jsonNode.get("title").asText();
        String description = jsonNode.get("description").asText();

        commentValidator.validateTitle(title);
        commentValidator.validateDescription(description);

        return commentValidator;
    }
}
