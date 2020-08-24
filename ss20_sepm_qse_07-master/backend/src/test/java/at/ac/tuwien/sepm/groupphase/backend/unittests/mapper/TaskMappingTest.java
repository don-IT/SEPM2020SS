package at.ac.tuwien.sepm.groupphase.backend.unittests.mapper;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalTaskDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EnclosureTaskDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TaskDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalTaskMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EnclosureTaskMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TaskMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.service.EnclosureService;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TaskMappingTest implements TestData {

    @Autowired
    TaskMapper taskMapper;

    @Autowired
    AnimalTaskMapper animalTaskMapper;

    @Autowired
    EnclosureTaskMapper enclosureTaskMapper;

    @MockBean
    EnclosureService enclosureService;

    private Employee anmial_caretaker = Employee.builder()
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .name(NAME_ANIMAL_CARE_EMPLOYEE)
        .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .type(TYPE_ANIMAL_CARE_EMPLOYEE)
        .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
        .build();


    private Task task_assigned = Task.builder()
        .id(2L)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(anmial_caretaker)
        .build();

    private Task task_assigned_high_priority = Task.builder()
        .id(2L)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(anmial_caretaker)
        .priority(true)
        .build();

    private Task task_not_assigned = Task.builder()
        .id(2L)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.NOT_ASSIGNED)
        .build();


    private TaskDto taskDto = TaskDto.builder()
        .id(2L)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .priority(true)
        .build();

    private Animal animal = Animal.builder()
        .id(2L)
        .name("Brandy")
        .description("racing Horce")
        .enclosure(null)
        .species("race")
        .publicInformation(null)
        .build();

    private Enclosure enclosure = Enclosure.builder()
        .id(4L)
        .name("Ocean")
        .description("plenty species of see animals")
        .publicInfo("public Info")
        .picture(null)
        .build();

    private AnimalTask animalTask = AnimalTask.builder()
        .id(2L)
        .subject(animal)
        .task(task_assigned_high_priority)
        .build();

    private AnimalTask animalTaskNotAssigned = AnimalTask.builder()
        .id(2L)
        .subject(animal)
        .task(task_not_assigned)
        .build();

    private EnclosureTask enclosureTaskTest = EnclosureTask.builder()
        .id(4L)
        .subject(enclosure)
        .task(task_assigned)
        .build();

    private EnclosureTask enclosureTaskNotAssigned = EnclosureTask.builder()
        .id(4L)
        .subject(enclosure)
        .task(task_not_assigned)
        .build();

    private EnclosureTaskDto enclosureTaskDto = EnclosureTaskDto.builder()
        .id(124L)
        .title("name")
        .description("description")
        .startTime(taskDto.getStartTime())
        .endTime(taskDto.getEndTime())
        .assignedEmployeeUsername(null)
        .enclosureId(4L)
        .build();

    @Test
    public void testEnclosureTaskToEnclosureTaskDto() {
        EnclosureTaskDto enclosureTaskDto = enclosureTaskMapper.enclosureTaskToEclosureTaskDto(enclosureTaskTest);
        assertAll(
            () -> assertEquals(enclosureTaskDto.getId(), enclosureTaskTest.getId()));
           // () -> assertEquals(enclosureTaskDto.getAssignedEmployeeUsername(), enclosureTaskTest.getTask().getAssignedEmployee()),
           // () -> assertEquals(enclosureTaskDto.getTitle(), enclosureTaskTest.getTask().getTitle()),
           // () -> assertEquals(enclosureTaskDto.getDescription(), enclosureTaskTest.getTask().getDescription()));
    }

    @Test
    public void testEnclosureTaskDtoToEnclosureTask() {
        Mockito.when(enclosureService.findById(4L)).thenReturn(enclosure);
        EnclosureTask enclosureTask = enclosureTaskMapper.enclosureTaskDtoToEclosureTask(enclosureTaskDto);
        assertAll(
            () -> assertEquals(enclosureTask.getId(), enclosureTaskDto.getId()),
            () -> assertEquals(enclosureTask.getTask().getTitle(), enclosureTaskDto.getTitle()),
            () -> assertEquals(enclosureTask.getTask().getDescription(), enclosureTaskDto.getDescription()));
    }

    @Test
    public void testTaskToTaskDto() {
        TaskDto taskDto = taskMapper.taskToTaskDto(task_assigned);
        assertAll(
            () -> assertEquals(taskDto.getId(), task_assigned.getId()),
            () -> assertEquals(taskDto.getTitle(), task_assigned.getTitle()),
            () -> assertEquals(taskDto.getDescription(), task_assigned.getDescription()),
            () -> assertEquals(taskDto.getStartTime(), task_assigned.getStartTime()),
            () -> assertEquals(taskDto.getEndTime(), task_assigned.getEndTime()),
            () -> assertEquals(taskDto.isPriority(), task_assigned.isPriority()),
            () -> assertEquals(taskDto.getStatus(), task_assigned.getStatus()));
    }

    @Test
    public void testTaskDtoToTask() {
        Task task = taskMapper.taskDtoToTask(taskDto);
        assertAll(
            () -> assertEquals(taskDto.getId(), task.getId()),
            () -> assertEquals(taskDto.getTitle(), task.getTitle()),
            () -> assertEquals(taskDto.getDescription(), task.getDescription()),
            () -> assertEquals(taskDto.getStartTime(), task.getStartTime()),
            () -> assertEquals(taskDto.getEndTime(), task.getEndTime()),
            () -> assertEquals(taskDto.isPriority(), task.isPriority()),
            () -> assertEquals(taskDto.getStatus(), task.getStatus()));
    }


    @Test
    public void testAnimalTaskToAnimalTaskDto() {
        AnimalTaskDto animalTaskDto = animalTaskMapper.animalTaskToAnimalTaskDto(animalTask);
        assertAll(
            () -> assertEquals(animalTaskDto.getId(), animalTask.getId()),
            () -> assertEquals(animalTaskDto.getAssignedEmployeeUsername(), animalTask.getTask().getAssignedEmployee().getUsername()),
            () -> assertEquals(animalTaskDto.getAnimalId(),animalTask.getSubject().getId()));
    }

    @Test
    public void testAnimalTaskToAnimalTaskDtoAssignedEmloyeeNull() {
        AnimalTaskDto animalTaskDto = animalTaskMapper.animalTaskToAnimalTaskDto(animalTaskNotAssigned);
        assertAll(
            () -> assertEquals(animalTaskDto.getId(), animalTask.getId()),
            () -> assertEquals(animalTaskDto.getAssignedEmployeeUsername(), null),
            () -> assertEquals(animalTaskDto.getAnimalId(),animalTask.getSubject().getId()));
    }



}
