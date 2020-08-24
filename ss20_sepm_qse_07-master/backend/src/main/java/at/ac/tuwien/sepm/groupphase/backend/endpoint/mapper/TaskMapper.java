package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalTaskDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RepeatableTaskDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TaskDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TaskMapper {

    @Mapping(source = "event", target = "event")
    Task taskDtoToTask(TaskDto taskDto);

    @Mapping(source = "event", target = "event")
    TaskDto taskToTaskDto(Task task);

    @Mapping(source = "event", target = "event")
    @Mapping(source = "eventPicture", target = "eventPicture")
    Task repeatableTaskDtoToTask(RepeatableTaskDto repeatableTaskDto);

    default byte[] pictureStringToByte(String pictueString) {
        return pictueString==null?null:pictueString.getBytes();
    }

    default String pictureByteToString(byte[] pictueByte) {
        return pictueByte==null?null:new String(pictueByte);
    }
}
