package luckyhunter.tracker.mapper;

import luckyhunter.tracker.model.dto.TaskChangeDto;
import luckyhunter.tracker.model.dto.TaskDto;
import luckyhunter.tracker.model.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    Task toTask(TaskDto taskDto);
    TaskDto toTaskDto(Task task);

    TaskChangeDto toTaskChangeDto(Task task);
    Task toTask(TaskChangeDto taskChangeDto);

    void updateTaskFromDto(TaskChangeDto taskChangeDto, @MappingTarget Task existingTask);
}
