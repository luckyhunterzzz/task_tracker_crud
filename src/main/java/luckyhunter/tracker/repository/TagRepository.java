package luckyhunter.tracker.repository;

import luckyhunter.tracker.model.entity.Tag;
import org.hibernate.Session;

import java.util.List;

public class TagRepository {

    public List<Tag> getAllTags(Session session) {
        return session.createQuery("FROM Tag", Tag.class)
                .getResultList();
    }

    public Tag getTagById(Session session, long tagId) {
        return session.get(Tag.class, tagId);
    }

    public Tag createTag(Session session, Tag tag) {
        session.persist(tag);
        return tag;
    }

    public Tag updateTag(Session session, Tag tag) {
        Tag existingTag = session.get(Tag.class, tag.getId());
        if (existingTag != null) {
            existingTag.setName(tag.getName());
            existingTag.setColor(tag.getColor());
            session.merge(existingTag);
            return existingTag;
        }
        return null;
    }

    public boolean deleteTag(Session session, int tagId) {
        Tag tag = session.get(Tag.class, tagId);
        if (tag != null) {
            session.remove(tag);
            return true;
        }
        return false;
    }
}
