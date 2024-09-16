package luckyhunter.tracker.repository;

import luckyhunter.tracker.model.entity.Comment;
import org.hibernate.Session;

import java.util.List;

public class CommentRepository {
    public List<Comment> getCommentsByTaskId(Session session, Long taskId) {
        return session.createQuery("FROM Comment c WHERE c.task.id = :taskId", Comment.class)
                .setParameter("taskId", taskId)
                .getResultList();
    }

    public Comment getCommentByCommentId(Session session, Long commentId) {
        return session.get(Comment.class, commentId);
    }

    public void createComment(Session session, Comment comment) {
        session.persist(comment);
    }

    public void updateComment(Session session, Comment comment) {
        session.merge(comment);
    }

    public boolean deleteComment(Session session, int commentId) {
        Comment comment = session.get(Comment.class, commentId);
        if (comment != null) {
            session.remove(comment);
            return true;
        }
        return false;
    }
}
