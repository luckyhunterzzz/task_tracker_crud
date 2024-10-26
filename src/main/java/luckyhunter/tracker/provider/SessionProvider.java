package luckyhunter.tracker.provider;

import org.hibernate.SessionFactory;

public interface SessionProvider {
    SessionFactory getSessionFactory();
}
