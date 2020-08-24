package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.entity.EnclosureTask;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureTaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.EnclosureService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomTaskService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class EnclosureTaskServiceTest {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    EnclosureService enclosureService;

    @Autowired
    CustomTaskService CustomTaskService;

    @MockBean
    EnclosureTaskRepository enclosureTaskRepository;

    private Enclosure enclosureDetailed = Enclosure.builder()
        .name(NAME_LION_ENCLOSURE)
        .description(DESCRIPTION_LION_ENCLOSURE)
        .publicInfo(PUBLIC_INFO_LION_ENCLOSURE)
        .picture(PICTURE_LION_ENCLOSURE)
        .build();

    private Employee animal_caretaker = Employee.builder()
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .name(NAME_ANIMAL_CARE_EMPLOYEE)
        .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .type(TYPE_ANIMAL_CARE_EMPLOYEE)
        .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
        .workTimeStart(TEST_LOCAL_TIME_START)
        .workTimeEnd(TEST_LOCAL_TIME_END)
        .build();

    private Employee animal_doctor = Employee.builder()
        .username("Doctor")
        .name(NAME_ANIMAL_CARE_EMPLOYEE)
        .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .type(EmployeeType.DOCTOR)
        .email("doctor@gmail.com")
        .workTimeStart(TEST_LOCAL_TIME_START)
        .workTimeEnd(TEST_LOCAL_TIME_END)
        .build();

    private Task task_assigned = Task.builder()
        .id(2L)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(animal_caretaker)
        .build();

    private Task task_assigned_high_priority = Task.builder()
        .id(2L)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(animal_caretaker)
        .priority(true)
        .build();

    private Task task_assigned1 = Task.builder()
        .id(2L)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(animal_doctor)
        .build();

    private EnclosureTask  enclosureTask = EnclosureTask.builder()
        .task(task_assigned)
        .subject(enclosureDetailed)
        .id(1L)
        .build();


    @Test
    public void saveEnclosureTaskwhenAssigningToDoctorWillThrowAnException() {


        Mockito.when(enclosureTaskRepository.save(EnclosureTask.builder().id(task_assigned.getId()).subject(enclosureDetailed).build())).thenReturn(enclosureTask);
        //(task_assigned,enclosureDetailed)).thenReturn(enclosureTask);
        employeeService.createEmployee(animal_doctor);
        EnclosureTask result;
        Exception exception = assertThrows(Exception.class, () -> {
            CustomTaskService.createEnclosureTask(task_assigned1,enclosureDetailed);
        });
    }

    //@Test
    public void saveEnclosureTaskbyGivingAllValues() {

        Mockito.when(enclosureTaskRepository.save(EnclosureTask.builder().id(task_assigned.getId()).subject(enclosureDetailed).build())).thenReturn(enclosureTask);
        //(task_assigned,enclosureDetailed)).thenReturn(enclosureTask);

        enclosureService.create(enclosureDetailed);

        EnclosureTask result = CustomTaskService.createEnclosureTask(task_assigned,enclosureDetailed);


        assertAll(
            () -> assertEquals(enclosureTask, result)
        );
    }

}
