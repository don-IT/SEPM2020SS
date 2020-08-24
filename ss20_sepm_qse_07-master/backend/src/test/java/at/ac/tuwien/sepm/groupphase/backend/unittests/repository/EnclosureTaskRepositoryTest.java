package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class EnclosureTaskRepositoryTest {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    UserLoginRepository userLoginRepository;

    @Autowired
    EnclosureRepository enclosureRepository;

    @Autowired
    EnclosureTaskRepository enclosureTaskRepository;

    private final UserLogin animal_caretaker_login = UserLogin.builder()
        .isAdmin(false)
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private final Employee anmial_caretaker = Employee.builder()
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .name(NAME_ANIMAL_CARE_EMPLOYEE)
        .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .type(TYPE_ANIMAL_CARE_EMPLOYEE)
        .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
        .workTimeStart(TEST_LOCAL_TIME_START)
        .workTimeEnd(TEST_LOCAL_TIME_END)
        .build();

    private final UserLogin doctor_login = UserLogin.builder()
        .isAdmin(false)
        .username(USERNAME_DOCTOR_EMPLOYEE)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private final Employee doctor = Employee.builder()
        .username(USERNAME_DOCTOR_EMPLOYEE)
        .name(NAME_ANIMAL_CARE_EMPLOYEE)
        .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .type(TYPE_DOCTOR_EMPLOYEE)
        .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
        .workTimeStart(TEST_LOCAL_TIME_START)
        .workTimeEnd(TEST_LOCAL_TIME_END)
        .build();

    private Task task_not_assigned = Task.builder()
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.NOT_ASSIGNED)
        .build();

    private Task task_assigned = Task.builder()
        .id(null)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(anmial_caretaker)
        .build();

    private Task task_assigned2 = Task.builder()
        .id(null)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(anmial_caretaker)
        .build();

    private Task task_assigned3 = Task.builder()
        .id(null)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(anmial_caretaker)
        .build();

    private Task event_assigned = Task.builder()
        .id(null)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(anmial_caretaker)
        .event(true)
        .build();

    private Task event_assigned2 = Task.builder()
        .id(null)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(anmial_caretaker)
        .event(true)
        .build();

    private Task event_assigned3 = Task.builder()
        .id(null)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .assignedEmployee(anmial_caretaker)
        .event(true)
        .build();

    private Enclosure barn = Enclosure.builder()
        .name("Barn")
        .build();

    @BeforeEach
    public void beforeEach() {
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
        enclosureTaskRepository.deleteAllAndBaseTasks();
    }

    @Test
    public void givenNothing_whenSaveEnclosureTask_createsEnclosureTask() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Enclosure enclosure = enclosureRepository.save(barn);

        Task createdTask = taskRepository.save(task_assigned);

       enclosureTaskRepository.save(EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build());

        EnclosureTask et = enclosureTaskRepository.findEnclosureTaskById(createdTask.getId());

        assertEquals(enclosure, et.getSubject());
        assertEquals(createdTask, et.getTask());
    }

    @Test
    public void givenNothing_whenSaveEnclosureEvent_createsEnclosureEvent() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Enclosure enclosure = enclosureRepository.save(barn);

        Task createdEvent = taskRepository.save(event_assigned);

        enclosureTaskRepository.save(EnclosureTask.builder()
            .id(createdEvent.getId())
            .subject(enclosure)
            .build());

        EnclosureTask et = enclosureTaskRepository.findEnclosureEventById(createdEvent.getId()).get();

        assertEquals(enclosure, et.getSubject());
        assertEquals(createdEvent, et.getTask());
    }

    @Test
    public void givenIdOfEnclosureTaskNotEvent_searchingForEnclosureEvent_thenThrowsNoSuchElementException()
    {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Enclosure enclosure = enclosureRepository.save(barn);

        Task createdTask = taskRepository.save(task_assigned);

        enclosureTaskRepository.save(EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build());

        assertThrows(java.util.NoSuchElementException.class, () -> enclosureTaskRepository.findEnclosureEventById(createdTask.getId()).get());
    }

    @Test
    public void givenNothing_searchingForEnclosureAssignedToEnclosureTask_thenFindEnclosure() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Enclosure enclosure = enclosureRepository.save(barn);

        Task createdTask = taskRepository.save(task_assigned);

        enclosureTaskRepository.save(EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build());

        Enclosure e = enclosureTaskRepository.getTaskSubjectById(createdTask.getId());

        assertEquals(enclosure, e);
    }

    @Test
    public void givenNothing_searchingForEnclosureTasksAssignedToEnclosure_thenFindAllAssignedEnclosureTasksAndTasks() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Enclosure enclosure = enclosureRepository.save(barn);

        Task createdTask = taskRepository.save(task_assigned);
        EnclosureTask ec1 = EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        EnclosureTask ec2 = EnclosureTask.builder()
            .id(createdTask2.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        EnclosureTask ec3 = EnclosureTask.builder()
            .id(createdTask3.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec3);


        List<EnclosureTask> etl = enclosureTaskRepository
            .findAllEnclosureTasksBySubject_Id(enclosure.getId());

        assertEquals(3, etl.size());
    }

    @Test
    public void givenNothing_searchingForEnclosureEventsAssignedToEnclosure_thenFindAllAssignedEnclosureEvents() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Enclosure enclosure = enclosureRepository.save(barn);

        Task createdTask = taskRepository.save(event_assigned);
        EnclosureTask ec1 = EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(event_assigned2);
        EnclosureTask ec2 = EnclosureTask.builder()
            .id(createdTask2.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(event_assigned3);
        EnclosureTask ec3 = EnclosureTask.builder()
            .id(createdTask3.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec3);


        List<EnclosureTask> eventList = enclosureTaskRepository
            .findAllEnclosureEventsBySubject_Id(enclosure.getId());

        assertEquals(3, eventList.size());
    }

    @Test
    public void givenEventsAndTasksOnEnclosure_searchingForEnclosureTaskAssignedToEnclosure_thenFindAllAssignedEnclosureEventsAndTasks() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Enclosure enclosure = enclosureRepository.save(barn);

        Task createdTask = taskRepository.save(event_assigned);
        EnclosureTask ec1 = EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(event_assigned2);
        EnclosureTask ec2 = EnclosureTask.builder()
            .id(createdTask2.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        EnclosureTask ec3 = EnclosureTask.builder()
            .id(createdTask3.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec3);


        List<EnclosureTask> eventList = enclosureTaskRepository
            .findAllEnclosureEventsBySubject_Id(enclosure.getId());

        List<EnclosureTask> etl = enclosureTaskRepository
            .findAllEnclosureTasksBySubject_Id(enclosure.getId());

        assertEquals(2, eventList.size());
        assertEquals(3, etl.size());
    }

    @Test
    public void givenExistingEnclosure_deletingAllEnclosureTasksAndBaseTasksAssignedToEnclosure_thenDeleteAllAssignedEnclosureTasksAndBaseTasks() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Enclosure enclosure = enclosureRepository.save(barn);

        Task createdTask = taskRepository.save(task_assigned);
        EnclosureTask ec1 = EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        EnclosureTask ec2 = EnclosureTask.builder()
            .id(createdTask2.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        EnclosureTask ec3 = EnclosureTask.builder()
            .id(createdTask3.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec3);


        List<EnclosureTask> etl = enclosureTaskRepository
            .findAllEnclosureTasksBySubject_Id(enclosure.getId());

        assertEquals(3, etl.size());

        enclosureTaskRepository.deleteAllAndBaseTasksBySubject_Id(enclosure.getId());
        etl = enclosureTaskRepository.findAllEnclosureTasksBySubject_Id(enclosure.getId());
        assertEquals(0, etl.size());

        List<Task> tl = taskRepository.findAll();
        assertEquals(0, tl.size());

    }

    @Test
    public void givenNothing_deletingAllEnclosureTasksAndBaseTasks_thenDeleteAllAssignedEnclosureTasksAndBaseTasks() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Enclosure enclosure = enclosureRepository.save(barn);

        Task createdTask = taskRepository.save(task_assigned);
        EnclosureTask ec1 = EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        EnclosureTask ec2 = EnclosureTask.builder()
            .id(createdTask2.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        EnclosureTask ec3 = EnclosureTask.builder()
            .id(createdTask3.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec3);


        List<EnclosureTask> etl = enclosureTaskRepository
            .findAllEnclosureTasksBySubject_Id(enclosure.getId());

        assertEquals(3, etl.size());

        enclosureTaskRepository.deleteAllAndBaseTasks();
        etl = enclosureTaskRepository.findAllEnclosureTasksBySubject_Id(enclosure.getId());
        assertEquals(0, etl.size());

        List<Task> tl = taskRepository.findAll();
        assertEquals(0, tl.size());

    }

    @Test
    public void givenValidId_deletingEnclosureTasksAndBaseTasksById_thenDeleteEnclosureTasksAndBaseTasksWithGivenId() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Enclosure enclosure = enclosureRepository.save(barn);

        Task createdTask = taskRepository.save(task_assigned);
        EnclosureTask ec1 = EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        EnclosureTask ec2 = EnclosureTask.builder()
            .id(createdTask2.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec2);



        List<EnclosureTask> etl = enclosureTaskRepository
            .findAllEnclosureTasksBySubject_Id(enclosure.getId());

        assertEquals(2, etl.size());

        enclosureTaskRepository.deleteEnclosureTaskAndBaseTaskById(createdTask.getId());
        etl = enclosureTaskRepository.findAllEnclosureTasksBySubject_Id(enclosure.getId());
        assertEquals(1, etl.size());

        List<Task> tl = taskRepository.findAll();
        assertEquals(1, tl.size());

        assertNull(enclosureTaskRepository.findEnclosureTaskById(createdTask.getId()));

        assertEquals(true, taskRepository.findById(createdTask.getId()).isEmpty());

    }

    @Test
    public void givenExistingEnclosure_deletingAllEnclosureTasksAssignedToEnclosure_thenDeleteAllAssignedEnclosureTasks() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Enclosure enclosure = enclosureRepository.save(barn);

        Task createdTask = taskRepository.save(task_assigned);
        EnclosureTask ec1 = EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        EnclosureTask ec2 = EnclosureTask.builder()
            .id(createdTask2.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        EnclosureTask ec3 = EnclosureTask.builder()
            .id(createdTask3.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec3);


        List<EnclosureTask> etl = enclosureTaskRepository
            .findAllEnclosureTasksBySubject_Id(enclosure.getId());

        assertEquals(3, etl.size());

        enclosureTaskRepository.deleteAllBySubject_Id(enclosure.getId());
        etl = enclosureTaskRepository.findAllEnclosureTasksBySubject_Id(enclosure.getId());
        assertEquals(0, etl.size());

        List<Task> tl = taskRepository.findAll();
        assertEquals(3, tl.size());

    }

    @Test
    public void givenNothing_deletingAllEnclosureTasks_thenDeleteAllAssignedEnclosureTasks() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Enclosure enclosure = enclosureRepository.save(barn);

        Task createdTask = taskRepository.save(task_assigned);
        EnclosureTask ec1 = EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        EnclosureTask ec2 = EnclosureTask.builder()
            .id(createdTask2.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        EnclosureTask ec3 = EnclosureTask.builder()
            .id(createdTask3.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec3);


        List<EnclosureTask> etl = enclosureTaskRepository
            .findAllEnclosureTasksBySubject_Id(enclosure.getId());

        assertEquals(3, etl.size());

        enclosureTaskRepository.deleteAll();
        etl = enclosureTaskRepository.findAllEnclosureTasksBySubject_Id(enclosure.getId());
        assertEquals(0, etl.size());

        List<Task> tl = taskRepository.findAll();
        assertEquals(3, tl.size());

    }

    @Test
    public void givenValidId_deletingEnclosureTasksById_thenDeleteEnclosureTasksWithGivenId() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Enclosure enclosure = enclosureRepository.save(barn);

        Task createdTask = taskRepository.save(task_assigned);
        EnclosureTask ec1 = EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        EnclosureTask ec2 = EnclosureTask.builder()
            .id(createdTask2.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec2);



        List<EnclosureTask> etl = enclosureTaskRepository
            .findAllEnclosureTasksBySubject_Id(enclosure.getId());

        assertEquals(2, etl.size());

        enclosureTaskRepository.deleteById(createdTask.getId());
        etl = enclosureTaskRepository.findAllEnclosureTasksBySubject_Id(enclosure.getId());
        assertEquals(1, etl.size());

        List<Task> tl = taskRepository.findAll();
        assertEquals(2, tl.size());

        assertNull(enclosureTaskRepository.findEnclosureTaskById(createdTask.getId()));

        assertEquals(false, taskRepository.findById(createdTask.getId()).isEmpty());

    }

    @Test
    public void taskSearch_allSearchParametersNullReturnAll() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Enclosure enclosure = enclosureRepository.save(barn);

        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Task createdTask = taskRepository.save(task_assigned);
        EnclosureTask ec1 = EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        EnclosureTask ec2 = EnclosureTask.builder()
            .id(createdTask2.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        EnclosureTask ec3 = EnclosureTask.builder()
            .id(createdTask3.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec3);
        Employee nullEmployee = Employee.builder().build();
        List<EnclosureTask> enclosureTasks = enclosureTaskRepository.findFilteredTasks(null, Task.builder().assignedEmployee(nullEmployee).build());
        assertEquals(enclosureTasks.size(), 3);
    }

    @Test
    public void taskSearch_searchUsername() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        userLoginRepository.save(doctor_login);
        employeeRepository.save(doctor);
        Enclosure enclosure = enclosureRepository.save(barn);

        Employee caretaker = employeeRepository.findEmployeeByUsername(USERNAME_DOCTOR_EMPLOYEE);

        task_assigned.setAssignedEmployee(caretaker);

        Task createdTask = taskRepository.save(task_assigned);
        EnclosureTask ec1 = EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        EnclosureTask ec2 = EnclosureTask.builder()
            .id(createdTask2.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        EnclosureTask ec3 = EnclosureTask.builder()
            .id(createdTask3.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec3);
        List<EnclosureTask> enclosureTasks = enclosureTaskRepository.findFilteredTasks(null, Task.builder().assignedEmployee(caretaker).build());
        assertEquals(enclosureTasks.size(), 1);
    }

    @Test
    public void taskSearch_searchUsernameAndTitle() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        userLoginRepository.save(doctor_login);
        employeeRepository.save(doctor);
        Enclosure enclosure = enclosureRepository.save(barn);

        Employee caretaker = employeeRepository.findEmployeeByUsername(USERNAME_DOCTOR_EMPLOYEE);

        task_assigned.setAssignedEmployee(caretaker);

        Task createdTask = taskRepository.save(task_assigned);
        EnclosureTask ec1 = EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        EnclosureTask ec2 = EnclosureTask.builder()
            .id(createdTask2.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        EnclosureTask ec3 = EnclosureTask.builder()
            .id(createdTask3.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec3);
        Task searchTask = Task.builder().title(createdTask.getTitle().substring(2, 5)).assignedEmployee(caretaker).build();
        List<EnclosureTask> enclosureTasks = enclosureTaskRepository.findFilteredTasks(null, Task.builder().assignedEmployee(caretaker).build());
        assertEquals(enclosureTasks.size(), 1);
    }

    @Test
    public void taskSearch_searchEmployeeType() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        userLoginRepository.save(doctor_login);
        employeeRepository.save(doctor);
        Enclosure enclosure = enclosureRepository.save(barn);

        Employee caretaker = employeeRepository.findEmployeeByUsername(USERNAME_DOCTOR_EMPLOYEE);

        task_assigned.setAssignedEmployee(caretaker);

        Task createdTask = taskRepository.save(task_assigned);
        EnclosureTask ec1 = EnclosureTask.builder()
            .id(createdTask.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        EnclosureTask ec2 = EnclosureTask.builder()
            .id(createdTask2.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        EnclosureTask ec3 = EnclosureTask.builder()
            .id(createdTask3.getId())
            .subject(enclosure)
            .build();
        enclosureTaskRepository.save(ec3);
        Employee nullEmployee = Employee.builder().build();
        Task searchTask = Task.builder().assignedEmployee(nullEmployee).build();
        List<EnclosureTask> enclosureTasks = enclosureTaskRepository.findFilteredTasks(EmployeeType.DOCTOR, Task.builder().assignedEmployee(caretaker).build());
        assertEquals(enclosureTasks.size(), 1);
    }
}
