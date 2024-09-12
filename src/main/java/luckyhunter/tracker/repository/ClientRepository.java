package luckyhunter.tracker.repository;

import luckyhunter.tracker.model.entity.Client;
import org.hibernate.Session;

import java.util.List;

public class ClientRepository {
    public List<Client> getAllClients(Session session) {
        return session.createQuery("FROM Client", Client.class).getResultList();
    }

    public Client getClientByLogin(Session session, String login) {
        return session.createQuery("FROM Client c LEFT JOIN FETCH c.tasks " +
                        "WHERE c.login = :login", Client.class)
                .setParameter("login", login)
                .uniqueResult();
    }

    public Client createClient(Session session, Client client) {
        session.persist(client);
        return client;
    }

    public void updateClient(Session session, Client client) {
        session.merge(client);
    }

    public void deleteClient(Session session, Client client) {
        session.remove(client);
    }
}
