package luckyhunter.tracker.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import luckyhunter.tracker.model.dto.CommentDto;
import luckyhunter.tracker.service.CommentService;
import luckyhunter.tracker.validator.CommentValidator;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@WebServlet("/api/comments/*")
public class CommentServlet extends HttpServlet implements Servlet {
    private CommentService commentService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();

        commentService = (CommentService) servletContext.getAttribute("commentService");
        objectMapper = (ObjectMapper) servletContext.getAttribute("objectMapper");

        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String taskIdParam = req.getParameter("taskId");
        if (taskIdParam != null) {
            // *не забыть Вытягивание комментария по задаче
            Long taskId = Long.parseLong(taskIdParam);
            sendObjectAsJson(objectMapper, resp, commentService.getCommentsByTaskId(taskId));
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("taskId parameter is required");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");

        try {
            Long taskId = Long.parseLong(pathParts[1]);
            String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            CommentValidator commentValidator = commentService.parseToCommentDto(requestBody);
            if (commentValidator.getErrors().isEmpty()) {
                // *не забыть Создание комментария
                CommentDto commentDto = commentValidator.getCommentDto();
                CommentDto createdComment = commentService.createComment(commentDto, taskId);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                sendObjectAsJson(objectMapper, resp, createdComment);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", commentValidator.getErrors())));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage())));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        Long commentId = Long.parseLong(pathParts[1]);
        try {
            String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            CommentValidator commentValidator = commentService.parseToCommentDto(requestBody);
            if (commentValidator.getErrors().isEmpty()) {
                CommentDto commentDto = commentValidator.getCommentDto();
                // *не забыть внесение изменений в комментарий
                CommentDto updatedComment = commentService.updateComment(commentDto, commentId);
                resp.setStatus(HttpServletResponse.SC_OK);
                sendObjectAsJson(objectMapper, resp, updatedComment);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", commentValidator.getErrors())));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage())));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            int commentId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = commentService.deleteComment(commentId);
            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
