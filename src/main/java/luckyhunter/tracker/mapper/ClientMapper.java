package luckyhunter.tracker.mapper;

import luckyhunter.tracker.model.dto.*;
import luckyhunter.tracker.model.entity.Client;
import luckyhunter.tracker.model.entity.Comment;
import luckyhunter.tracker.model.entity.Tag;
import luckyhunter.tracker.model.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    ClientDto toClientDto(Client client);
    Client toClient(ClientDto clientDto);

    ClientToCreateDto toClientToCreateDto(Client client);
    Client toClient(ClientToCreateDto clientToCreateDto);
    void updateClientFromDto(ClientToCreateDto clientToUpdate, @MappingTarget Client existingClient);

    TaskDto toTaskDto(Task task);
    TagDto toTagDto(Tag tag);
    CommentDto toCommentDto(Comment comment);
}
