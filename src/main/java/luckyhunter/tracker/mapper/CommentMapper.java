package luckyhunter.tracker.mapper;

import luckyhunter.tracker.model.dto.CommentDto;
import luckyhunter.tracker.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    Comment toComment(CommentDto commentDto);

    CommentDto toCommentDto(Comment comment);
}
