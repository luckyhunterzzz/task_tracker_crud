package luckyhunter.tracker.provider;

import luckyhunter.tracker.model.entity.Client;
import luckyhunter.tracker.model.entity.Comment;
import luckyhunter.tracker.model.entity.Tag;
import luckyhunter.tracker.model.entity.Task;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class TaskTrackerSessionProvider implements SessionProvider {

    @Override
    public SessionFactory getSessionFactory() {

        return new Configuration()
                .addAnnotatedClass(Tag.class)
                .addAnnotatedClass(Client.class)
                .addAnnotatedClass(Task.class)
                .addAnnotatedClass(Comment.class)
                .buildSessionFactory();
    }
}
