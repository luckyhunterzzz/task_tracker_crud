package luckyhunter.tracker.mapper;

import luckyhunter.tracker.model.dto.TagDto;
import luckyhunter.tracker.model.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TagMapper {

    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    TagDto toTagDto(Tag tag);
    Tag toTag(TagDto tagDto);
}
