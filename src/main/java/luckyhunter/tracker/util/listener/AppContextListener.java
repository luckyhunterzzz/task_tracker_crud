package luckyhunter.tracker.util.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import luckyhunter.tracker.mapper.ClientMapper;
import luckyhunter.tracker.mapper.CommentMapper;
import luckyhunter.tracker.mapper.TagMapper;
import luckyhunter.tracker.mapper.TaskMapper;
import luckyhunter.tracker.provider.SessionProvider;
import luckyhunter.tracker.provider.TaskTrackerSessionProvider;
import luckyhunter.tracker.repository.ClientRepository;
import luckyhunter.tracker.repository.CommentRepository;
import luckyhunter.tracker.repository.TagRepository;
import luckyhunter.tracker.repository.TaskRepository;
import luckyhunter.tracker.service.ClientService;
import luckyhunter.tracker.service.CommentService;
import luckyhunter.tracker.service.TagService;
import luckyhunter.tracker.service.TaskService;
import org.hibernate.SessionFactory;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        SessionProvider sessionProvider = new TaskTrackerSessionProvider();
        SessionFactory sessionFactory = sessionProvider.getSessionFactory();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        TagMapper tagMapper = TagMapper.INSTANCE;
        TagRepository tagRepository = new TagRepository();
        TagService tagService = new TagService(sessionFactory, tagRepository, tagMapper);

        ClientMapper clientMapper = ClientMapper.INSTANCE;
        ClientRepository clientRepository = new ClientRepository();
        ClientService clientService = new ClientService(sessionFactory, clientRepository, clientMapper);

        CommentMapper commentMapper = CommentMapper.INSTANCE;
        CommentRepository commentRepository = new CommentRepository();
        CommentService commentService = new CommentService(sessionFactory, commentRepository, commentMapper);

        TaskMapper taskMapper = TaskMapper.INSTANCE;
        TaskRepository taskRepository = new TaskRepository();
        TaskService taskService = new TaskService(sessionFactory, taskRepository, taskMapper, clientRepository, tagRepository);

        servletContext.setAttribute("tagService", tagService);
        servletContext.setAttribute("taskService", taskService);
        servletContext.setAttribute("commentService", commentService);
        servletContext.setAttribute("clientService", clientService);
        servletContext.setAttribute("objectMapper", objectMapper);
        servletContext.setAttribute("sessionFactory", sessionFactory);

        ServletContextListener.super.contextInitialized(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        SessionFactory sessionFactory = (SessionFactory) sce.getServletContext().getAttribute("sessionFactory");
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
