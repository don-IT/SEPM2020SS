package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalTaskDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.AnimalTask;
import org.springframework.stereotype.Component;

@Component
public class AnimalTaskMapper {

    public AnimalTaskDto animalTaskToAnimalTaskDto(AnimalTask animalTask){
        return AnimalTaskDto.builder()
            .id(animalTask.getId())
            .title(animalTask.getTask().getTitle())
            .description(animalTask.getTask().getDescription())
            .startTime(animalTask.getTask().getStartTime())
            .endTime(animalTask.getTask().getEndTime())
            .assignedEmployeeUsername(animalTask.getTask().getAssignedEmployee()==null?null:animalTask.getTask().getAssignedEmployee().getUsername())
            .status(animalTask.getTask().getStatus())
            .animalName(animalTask.getSubject().getName())
            .animalId(animalTask.getSubject().getId())
            .priority(animalTask.getTask().isPriority())
            .build();
    }
}
