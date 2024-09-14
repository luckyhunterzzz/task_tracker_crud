package luckyhunter.tracker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import luckyhunter.tracker.mapper.ClientMapper;
import luckyhunter.tracker.model.dto.ClientDto;
import luckyhunter.tracker.model.dto.ClientToCreateDto;
import luckyhunter.tracker.model.entity.Client;
import luckyhunter.tracker.repository.ClientRepository;
import luckyhunter.tracker.validator.ClientValidator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ClientService {

    private final SessionFactory sessionFactory;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientService(SessionFactory sessionFactory, ClientRepository clientRepository, ClientMapper clientMapper) {
        this.sessionFactory = sessionFactory;
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public List<ClientDto> getAllClients() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            List<ClientDto> result = clientRepository.getAllClients(session)
                    .stream()
                    .map(clientMapper :: toClientDto)
                    .toList();
            session.getTransaction().commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public ClientDto getClientDataByLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            Client client = clientRepository.getClientByLogin(session, login);
            if (client == null) {
                return null;
            }
            ClientDto clientDto = clientMapper.toClientDto(client);
            return clientDto;
        }
    }

    public void createClient(ClientToCreateDto clientToCreate) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                Client client = clientMapper.toClient(clientToCreate);
                clientRepository.createClient(session, client);
                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace(System.err);
                session.getTransaction().rollback();
                throw e;
            }
        }
    }

    public void editClient(String clientLogin, ClientToCreateDto clientToUpdate) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                Client existingClient = clientRepository.getClientByLogin(session, clientLogin);
                if (existingClient == null) {
                    throw new IllegalArgumentException("Клиент с логином " + clientLogin + " не найден");
                }

                clientMapper.updateClientFromDto(clientToUpdate, existingClient);
                clientRepository.updateClient(session, existingClient);

                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        }
    }

    public void deleteClient(String login) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                Client client = clientRepository.getClientByLogin(session, login);
                if (client == null) {
                    throw new IllegalArgumentException("Клиент с логином " + login + " не найден");
                }
                clientRepository.deleteClient(session, client);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        }
    }

    public ClientValidator parseToClientChangeDto(String body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(body);
        ClientValidator clientValidator = new ClientValidator();

        String firstName = jsonNode.get("firstName").asText();
        String lastName = jsonNode.get("lastName").asText();
        String email = jsonNode.get("email").asText();
        String phoneNumber = jsonNode.get("phoneNumber").asText();
        String birthDate = jsonNode.get("birthDate").asText();
        String login = jsonNode.get("login").asText();
        String password = jsonNode.get("password").asText();

        clientValidator.validateFirstName(firstName);
        clientValidator.validateLastName(lastName);
        clientValidator.validateEmail(email);
        clientValidator.validatePhoneNumber(phoneNumber);
        clientValidator.validateDateOfBirth(birthDate);
        clientValidator.validateLogin(login);
        clientValidator.validatePassword(password);

        return clientValidator;
    }

    public boolean validateCredentials(String login, String password) {
        try (Session session = sessionFactory.openSession()) {
            Client client = clientRepository.getClientByLogin(session, login);
            if (client != null) {
                return client.getPassword().equals(password);
            }
            return false;
        }
    }

    public void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBody);
        String login = jsonNode.get("login").asText();
        String password = jsonNode.get("password").asText();

        boolean isValid = validateCredentials(login, password);

        resp.setContentType("application/json");
        resp.getWriter().write(objectMapper.writeValueAsString(Collections.singletonMap("success", isValid)));
    }
}
