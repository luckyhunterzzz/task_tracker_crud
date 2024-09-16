package luckyhunter.tracker.repository;

import luckyhunter.tracker.model.entity.Comment;
import luckyhunter.tracker.model.entity.Task;
import org.hibernate.Session;

import java.util.List;

public class TaskRepository {
    public List<Task> getAllTasks(Session session) {
        return session.createQuery("FROM Task", Task.class).getResultList();
    }

    public void createTask(Session session, Task task) {
        session.merge(task);
    }

    public Task getTaskById(Session session, Long taskId) {
        return session.get(Task.class, taskId);
    }

    public void updateTask(Session session, Task task) {
        session.merge(task);
    }

    public void deleteTask(Session session, Task task) {
        session.remove(task);
    }
}
