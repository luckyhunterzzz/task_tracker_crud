package luckyhunter.tracker.servlet;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import luckyhunter.tracker.model.dto.TaskDto;
import luckyhunter.tracker.model.entity.Task;
import luckyhunter.tracker.service.TaskService;
import luckyhunter.tracker.validator.TaskValidator;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@WebServlet("/api/tasks/*")
public class TaskServlet extends HttpServlet implements Servlet {

    private TaskService taskService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();

        taskService = (TaskService) servletContext.getAttribute("taskService");
        objectMapper = (ObjectMapper) servletContext.getAttribute("objectMapper");
        // *не забыть Игнорирование некоторых полей DTO, для избегания ошибок сериализации
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // *не забыть Весь список задач (возможность для отладки) если путь пуст
            sendObjectAsJson(objectMapper, resp, taskService.getAllTasks());
        } else {
            String[] splits = pathInfo.split("/");
            if (splits.length == 2) {
                // *не забыть Инфо таски по ID
                String taskId = splits[1];
                try {
                    TaskDto task = taskService.getTaskById(Long.parseLong(taskId));
                    if (task != null) {
                        System.out.println("Task fetched: " + task);
                        sendObjectAsJson(objectMapper, resp, task);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write("Task not found");
                    }
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("Invalid task ID");
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid request");
            }
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        JsonNode jsonNode = objectMapper.readTree(requestBody);

        if (pathInfo != null && pathInfo.matches("/\\d+/tags")) {
            // *не забыть "Добавление нового тэга к задаче"
            try {
                long taskId = Long.parseLong(pathInfo.split("/")[1]);
                long tagId = jsonNode.get("tagId").asLong();

                TaskDto updatedTask = taskService.addTagToTask(taskId, tagId);

                if (updatedTask != null) {
                    sendObjectAsJson(objectMapper, resp, updatedTask);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("Task or Tag not found");
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Error adding tag to task: " + e.getMessage());
            }
        } else {
            try {
                // *не забыть Создание новой таски для клиента
                TaskValidator taskValidator = taskService.parseToTaskChangeDto(requestBody);
                if (!jsonNode.has("clientLogin")) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("Client login is missing");
                    return;
                }
                String clientLogin = jsonNode.get("clientLogin").asText();

                if(taskValidator.getErrors().isEmpty()) {
                    Task createdTask = taskService.createTask(taskValidator.getTaskChangeDto(), clientLogin);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    resp.getWriter().write(objectMapper.writeValueAsString(createdTask));
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", taskValidator.getErrors())));
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage())));
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid request: path info is missing");
            return;
        }

        String[] splits = pathInfo.split("/");
        if (splits.length < 2) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid request: task ID is missing");
            return;
        }

        long taskId;
        try {
            taskId = Long.parseLong(splits[1]);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid task ID");
            return;
        }

        String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        if (pathInfo.matches("/\\d+/status")) {
            // *не забыть Обновление статуса задачи
            try {
                JsonNode jsonNode = objectMapper.readTree(requestBody);
                String newStatus = jsonNode.get("status").asText();

                taskService.updateTaskStatus(taskId, newStatus);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("Task status updated successfully");
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Error updating task status: " + e.getMessage());
            }
        } else if (pathInfo.matches("/\\d+")) {
            // *не забыть Полное обновление задачи
            try {
                TaskValidator taskValidator = taskService.parseToTaskChangeDto(requestBody);

                if(taskValidator.getErrors().isEmpty()) {
                    taskService.updateTask(taskId, taskValidator.getTaskChangeDto());
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", taskValidator.getErrors())));
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage())));
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid request");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid request: missing path info");
            return;
        }

        if (pathInfo.matches("/\\d+")) {
            // *не забыть "Удаление задачи"
            long taskId = Long.parseLong(pathInfo.substring(1));
            try {
                boolean deleted = taskService.deleteTask(taskId);
                if (deleted) {
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("Task not found");
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Error deleting task: " + e.getMessage());
            }
        } else if (pathInfo.matches("/\\d+/tags/\\d+")) {
            // *не забыть "Удаление тэга с задачи"
            String[] pathParts = pathInfo.split("/");
            long taskId = Long.parseLong(pathParts[1]);
            long tagId = Long.parseLong(pathParts[3]);

            try {
                TaskDto updatedTask = taskService.removeTagFromTask(taskId, tagId);
                if (updatedTask != null) {
                    sendObjectAsJson(objectMapper, resp, updatedTask);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("Task or Tag not found");
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Error removing tag from task: " + e.getMessage());
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid request: unknown path format");
        }
    }
}
