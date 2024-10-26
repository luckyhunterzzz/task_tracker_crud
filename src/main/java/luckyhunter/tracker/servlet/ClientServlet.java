package luckyhunter.tracker.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import luckyhunter.tracker.model.dto.ClientDto;
import luckyhunter.tracker.model.dto.ClientToCreateDto;
import luckyhunter.tracker.service.ClientService;
import luckyhunter.tracker.validator.ClientValidator;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@WebServlet("/api/client/*")
public class ClientServlet extends HttpServlet implements Servlet {
    private ClientService clientService;
    private ObjectMapper objectMapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();
        clientService = (ClientService) servletContext.getAttribute("clientService");
        objectMapper = (ObjectMapper) servletContext.getAttribute("objectMapper");

        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            sendObjectAsJson(objectMapper, resp, clientService.getAllClients());
        } else {
            String clientIdentifier = pathInfo.substring(1);
            ClientDto clientData = clientService.getClientDataByLogin(clientIdentifier);

            if (clientData == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Клиент не найден");
            } else {
                sendObjectAsJson(objectMapper, resp, clientData);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if ("/login".equals(pathInfo)) {
                clientService.handleLogin(req, resp);
            } else {
                String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
                ClientValidator clientValidator = clientService.parseToClientChangeDto(requestBody);

                if (clientValidator.getErrors().isEmpty()) {
                    ClientToCreateDto clientToCreate = clientValidator.getClientToCreateDto();
                    clientService.createClient(clientToCreate);

                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("message", "Клиент успешно создан")));
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", clientValidator.getErrors())));
                }
            }
        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage())));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String clientLogin = req.getPathInfo().substring(1);
            if (clientLogin == null || clientLogin.isEmpty()) {
                throw new IllegalArgumentException("Логин клиента не указан");
            }

            String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            ClientValidator clientValidator = clientService.parseToClientChangeDto(requestBody);

            if (clientValidator.getErrors().isEmpty()) {
                ClientToCreateDto clientToUpdate = clientValidator.getClientToCreateDto();

                clientService.editClient(clientLogin, clientToUpdate);

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("message", "Клиент успешно обновлен")));
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", clientValidator.getErrors())));
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", e.getMessage())));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("error", "Внутренняя ошибка сервера")));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Логин клиента не указан");
            return;
        }
        String clientLogin = pathInfo.substring(1);
        try {
            clientService.deleteClient(clientLogin);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Клиент успешно удален");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при удалении клиента: " + e.getMessage());
        }
    }

}
