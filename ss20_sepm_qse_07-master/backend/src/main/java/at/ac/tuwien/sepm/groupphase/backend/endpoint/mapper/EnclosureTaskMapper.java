package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EnclosureTaskDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.entity.EnclosureTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.EnclosureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnclosureTaskMapper {

    private final EmployeeService employeeService;
    private final EnclosureService enclosureService;
    private final TaskMapper taskMapper;

    @Autowired
    public EnclosureTaskMapper(EmployeeService employeeService, EnclosureService enclosureService, TaskMapper taskMapper) {
        this.employeeService = employeeService;
        this.enclosureService = enclosureService;
        this.taskMapper = taskMapper;
    }

    public EnclosureTaskDto enclosureTaskToEclosureTaskDto(EnclosureTask enclosureTask) {
        if (enclosureTask == null) return null;
        return EnclosureTaskDto.builder()
            .id(enclosureTask.getId())
            .title(enclosureTask.getTask() != null ? enclosureTask.getTask().getTitle() : null)
            .description(enclosureTask.getTask().getDescription())
            .startTime(enclosureTask.getTask().getStartTime())
            .endTime(enclosureTask.getTask().getEndTime())
            .assignedEmployeeUsername(enclosureTask.getTask().getAssignedEmployee() == null ? null : enclosureTask.getTask().getAssignedEmployee().getUsername())
            .status(enclosureTask.getTask().getStatus())
            .enclosureName(enclosureTask.getSubject() != null ? enclosureTask.getSubject().getName() : null)
            .enclosureId(enclosureTask.getSubject() != null ? enclosureTask.getSubject().getId() : null)
            .priority(enclosureTask.getTask().isPriority())
            .build();
    }


    public EnclosureTask enclosureTaskDtoToEclosureTask(EnclosureTaskDto enclosureTaskDto) {

        if (enclosureTaskDto == null) return null;

        Employee employee = enclosureTaskDto.getAssignedEmployeeUsername() != null ? employeeService.findByUsername(enclosureTaskDto.getAssignedEmployeeUsername()) : null;

        Enclosure enclosure = enclosureTaskDto.getEnclosureId() != null ? enclosureService.findById(enclosureTaskDto.getEnclosureId()) : null;


        Task task = Task.builder()
            .id(enclosureTaskDto.getId())
            .title(enclosureTaskDto.getTitle())
            .description(enclosureTaskDto.getDescription())
            .startTime(enclosureTaskDto.getStartTime())
            .endTime(enclosureTaskDto.getEndTime())
            .status(enclosureTaskDto.getStatus())
            .priority(enclosureTaskDto.isPriority())
            .build();

        return EnclosureTask.builder()
            .id(enclosureTaskDto.getId())
            .task(task)
            .subject(enclosure)
            .build();
    }


}
