package luckyhunter.tracker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import luckyhunter.tracker.mapper.TagMapper;
import luckyhunter.tracker.model.dto.TagDto;
import luckyhunter.tracker.model.entity.Tag;
import luckyhunter.tracker.repository.TagRepository;
import luckyhunter.tracker.validator.TagValidator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Collections;
import java.util.List;

public class TagService {
    private final SessionFactory sessionFactory;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagService(SessionFactory sessionFactory, TagRepository tagRepository, TagMapper tagMapper) {
        this.sessionFactory = sessionFactory;
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    public List<TagDto> getAllTags() {

        try (Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            List<TagDto> result = tagRepository.getAllTags(session)
                    .stream()
                    .map(tagMapper::toTagDto)
                    .toList();

            session.getTransaction().commit();

            return result;

        } catch (Exception e) {

            e.printStackTrace();

            return Collections.emptyList();
        }
    }

    public TagDto getTagById(int tagId) {
        try (Session session = sessionFactory.openSession()) {
            Tag tag = tagRepository.getTagById(session, tagId);
            return tag != null ? tagMapper.toTagDto(tag) : null;
        }
    }

    public TagDto createTag(TagDto tagDto) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                Tag tag = tagMapper.toTag(tagDto);
                Tag createdTag = tagRepository.createTag(session, tag);
                session.getTransaction().commit();
                return tagMapper.toTagDto(createdTag);
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        }
    }

    public TagDto updateTag(TagDto tagDto) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                Tag tag = tagMapper.toTag(tagDto);
                Tag updatedTag = tagRepository.updateTag(session, tag);
                session.getTransaction().commit();
                return updatedTag != null ? tagMapper.toTagDto(updatedTag) : null;
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        }
    }

    public boolean deleteTag(int tagId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                boolean deleted = tagRepository.deleteTag(session, tagId);
                session.getTransaction().commit();
                return deleted;
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw e;
            }
        }
    }

    public TagValidator parseToTagDto(String body) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(body);
        TagValidator tagValidator = new TagValidator();

        String name = jsonNode.get("name").asText();
        String color = jsonNode.get("color").asText();

        tagValidator.validateName(name);
        tagValidator.validateHexColor(color);


        return tagValidator;
    }
}
