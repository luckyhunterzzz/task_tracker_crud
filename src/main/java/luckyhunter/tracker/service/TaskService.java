package luckyhunter.tracker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import luckyhunter.tracker.mapper.TaskMapper;
import luckyhunter.tracker.model.dto.TaskDto;
import luckyhunter.tracker.model.dto.TaskChangeDto;
import luckyhunter.tracker.model.entity.Client;
import luckyhunter.tracker.model.entity.Tag;
import luckyhunter.tracker.model.entity.Task;
import luckyhunter.tracker.repository.ClientRepository;
import luckyhunter.tracker.repository.TagRepository;
import luckyhunter.tracker.repository.TaskRepository;
import luckyhunter.tracker.validator.TaskValidator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Collections;
import java.util.List;

public class TaskService {
    private final SessionFactory sessionFactory;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ClientRepository clientRepository;
    private final TagRepository tagRepository;

    public TaskService(SessionFactory sessionFactory, TaskRepository taskRepository, TaskMapper taskMapper, ClientRepository clientRepository, TagRepository tagRepository) {
        this.sessionFactory = sessionFactory;
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.clientRepository = clientRepository;
        this.tagRepository = tagRepository;
    }

    public List<TaskDto> getAllTasks() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            List<Task> result = taskRepository.getAllTasks(session);

            session.getTransaction().commit();
            return result
                    .stream()
                    .map(taskMapper::toTaskDto)
                    .toList();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Task createTask(TaskChangeDto taskChangeDto, String clientLogin) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Client client = clientRepository.getClientByLogin(session, clientLogin);
            if (client == null) {
                throw new IllegalArgumentException("Клиент с логином " + clientLogin + " не найден");
            }

            Task task = taskMapper.toTask(taskChangeDto);
            task.setClient(client);

            taskRepository.createTask(session, task);

            session.getTransaction().commit();
            return task;
        }
    }

    public TaskDto getTaskById(Long taskId) {
        try (Session session = sessionFactory.openSession()) {

            Task task = taskRepository.getTaskById(session, taskId);

            return task != null ? taskMapper.toTaskDto(task) : null;
        }
    }

    public void updateTaskStatus(long taskId, String newStatus) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Task task = taskRepository.getTaskById(session, taskId);
            if (task != null) {
                task.setStatus(newStatus);
                taskRepository.updateTask(session, task);

                session.getTransaction().commit();
            } else {
                throw new IllegalArgumentException("Task not found with id: " + taskId);
            }
        }
    }

    public void updateTask(long taskId, TaskChangeDto taskChangeDto) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Task task = taskRepository.getTaskById(session, taskId);
            if (task != null) {
                taskMapper.updateTaskFromDto(taskChangeDto, task);
                taskRepository.updateTask(session, task);

                session.getTransaction().commit();
            } else {
                throw new IllegalArgumentException("Task not found with id: " + taskId);
            }
        }
    }

    public TaskDto addTagToTask(long taskId, long tagId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Task task = taskRepository.getTaskById(session, taskId);
            Tag tag = tagRepository.getTagById(session, tagId);

            if (task != null && tag != null) {
                if (!task.getTags().contains(tag)) {
                    task.getTags().add(tag);
                    taskRepository.updateTask(session, task);

                    session.getTransaction().commit();
                }
                return taskMapper.toTaskDto(task);
            } else {
                return null;
            }
        }
    }

    public TaskDto removeTagFromTask(long taskId, long tagId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Task task = taskRepository.getTaskById(session, taskId);
            Tag tag = tagRepository.getTagById(session, tagId);
            if (task != null && tag != null) {
                task.getTags().remove(tag);
                taskRepository.updateTask(session, task);

                session.getTransaction().commit();
                return taskMapper.toTaskDto(task);
            } else {
                return null;
            }
        }
    }

    public boolean deleteTask(long taskId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Task task = taskRepository.getTaskById(session, taskId);
            if (task != null) {
                taskRepository.deleteTask(session, task);

                session.getTransaction().commit();
                return true;
            } else {
                return false;
            }
        }
    }

    public TaskValidator parseToTaskChangeDto(String body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(body);
        TaskValidator taskValidator = new TaskValidator();

        String title = jsonNode.get("title").asText();
        String description = jsonNode.get("description").asText();
        String status = jsonNode.get("status").asText();
        String plannedStart = jsonNode.get("plannedStart").asText();
        String plannedEnd = jsonNode.get("plannedEnd").asText();

        taskValidator.validateTitle(title);
        taskValidator.validateDescription(description);
        taskValidator.validateStatus(status);
        taskValidator.validatePlannedStartTime(plannedStart);
        taskValidator.validatePlannedEndTime(plannedEnd);

        return taskValidator;
    }
}
