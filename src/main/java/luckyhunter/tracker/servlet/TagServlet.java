package luckyhunter.tracker.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import luckyhunter.tracker.model.dto.TagDto;
import luckyhunter.tracker.service.TagService;
import luckyhunter.tracker.validator.TagValidator;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@WebServlet("/api/tags/*")
public class TagServlet extends HttpServlet implements Servlet {

    private TagService tagService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();
        tagService = (TagService) servletContext.getAttribute("tagService");
        objectMapper = (ObjectMapper) servletContext.getAttribute("objectMapper");

        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // *не забыть Вытягивание всех тэгов
            sendObjectAsJson(objectMapper, resp, tagService.getAllTags());
        } else {
            try {
                // *не забыть Вытягивание одного тэга по ИД
                int tagId = Integer.parseInt(pathInfo.substring(1));
                TagDto tag = tagService.getTagById(tagId);
                if (tag != null) {
                    sendObjectAsJson(objectMapper, resp, tag);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("Tag not found");
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid tag ID");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // *не забыть "Сохдание тэга"
            String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            TagValidator tagValidator = tagService.parseToTagDto(requestBody);

            if (tagValidator.getErrors().isEmpty()) {
                TagDto createdTag = tagService.createTag(tagValidator.getTagDto());
                resp.setStatus(HttpServletResponse.SC_CREATED);
                sendObjectAsJson(objectMapper, resp, createdTag);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", tagValidator.getErrors())));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage())));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int tagId = Integer.parseInt(pathInfo.substring(1));
        // *не забыть "изменение тэга"
        try {
            String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            TagValidator tagValidator = tagService.parseToTagDto(requestBody);

            if (tagValidator.getErrors().isEmpty()) {
                TagDto tagDto = tagValidator.getTagDto();
                tagDto.setId(tagId);
                TagDto updatedTag = tagService.updateTag(tagDto);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                sendObjectAsJson(objectMapper, resp, updatedTag);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", tagValidator.getErrors())));
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
            // *не забыть "Удаление тэга"
            int tagId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = tagService.deleteTag(tagId);
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
