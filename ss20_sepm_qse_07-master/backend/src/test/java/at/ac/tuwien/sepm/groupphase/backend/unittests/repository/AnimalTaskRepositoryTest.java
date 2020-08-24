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
import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class AnimalTaskRepositoryTest {

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
    AnimalRepository animalRepository;

    @Autowired
    EnclosureTaskRepository enclosureTaskRepository;

    @Autowired
    AnimalTaskRepository animalTaskRepository;

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

    private Animal animal_basic = Animal.builder()
        .name("Barn")
        .species("adc")
        .description("asds")
        .publicInformation("acs")
        .build();

    @BeforeEach
    public void beforeEach() {
        animalTaskRepository.deleteAll();
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
        animalRepository.deleteAll();
    }

    @Test
    public void taskSearch_allSearchParametersNullReturnAll() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Animal animal = animalRepository.save(animal_basic);

        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);

        Task createdTask = taskRepository.save(task_assigned);
        AnimalTask ec1 = AnimalTask.builder()
            .id(createdTask.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        AnimalTask ec2 = AnimalTask.builder()
            .id(createdTask2.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        AnimalTask ec3 = AnimalTask.builder()
            .id(createdTask3.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec3);
        Employee nullEmployee = Employee.builder().build();
        List<AnimalTask> animalTasks = animalTaskRepository.findFilteredTasks(null, Task.builder().assignedEmployee(nullEmployee).build());
        assertEquals(animalTasks.size(), 3);
    }

    @Test
    public void taskSearch_searchUsername() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        userLoginRepository.save(doctor_login);
        employeeRepository.save(doctor);
        Animal animal = animalRepository.save(animal_basic);

        Employee caretaker = employeeRepository.findEmployeeByUsername(USERNAME_DOCTOR_EMPLOYEE);

        task_assigned.setAssignedEmployee(caretaker);

        Task createdTask = taskRepository.save(task_assigned);
        AnimalTask ec1 = AnimalTask.builder()
            .id(createdTask.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        AnimalTask ec2 = AnimalTask.builder()
            .id(createdTask2.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        AnimalTask ec3 = AnimalTask.builder()
            .id(createdTask3.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec3);
        List<AnimalTask> animalTasks = animalTaskRepository.findFilteredTasks(null, Task.builder().assignedEmployee(caretaker).build());
        assertEquals(animalTasks.size(), 1);
    }

    @Test
    public void taskSearch_searchUsernameAndTitle() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        userLoginRepository.save(doctor_login);
        employeeRepository.save(doctor);
        Animal animal = animalRepository.save(animal_basic);

        Employee caretaker = employeeRepository.findEmployeeByUsername(USERNAME_DOCTOR_EMPLOYEE);

        task_assigned.setAssignedEmployee(caretaker);

        Task createdTask = taskRepository.save(task_assigned);
        AnimalTask ec1 = AnimalTask.builder()
            .id(createdTask.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        AnimalTask ec2 = AnimalTask.builder()
            .id(createdTask2.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        AnimalTask ec3 = AnimalTask.builder()
            .id(createdTask3.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec3);
        Task searchTask = Task.builder().title(createdTask.getTitle().substring(2, 5)).assignedEmployee(caretaker).build();
        List<AnimalTask> animalTasks = animalTaskRepository.findFilteredTasks(null, searchTask);
        assertEquals(animalTasks.size(), 1);
    }

    @Test
    public void taskSearch_searchEmployeeType() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        userLoginRepository.save(doctor_login);
        employeeRepository.save(doctor);
        Animal animal = animalRepository.save(animal_basic);

        Employee caretaker = employeeRepository.findEmployeeByUsername(USERNAME_DOCTOR_EMPLOYEE);

        task_assigned.setAssignedEmployee(caretaker);

        Task createdTask = taskRepository.save(task_assigned);
        AnimalTask ec1 = AnimalTask.builder()
            .id(createdTask.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        AnimalTask ec2 = AnimalTask.builder()
            .id(createdTask2.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        AnimalTask ec3 = AnimalTask.builder()
            .id(createdTask3.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec3);
        Employee nullEmployee = Employee.builder().build();
        Task searchTask = Task.builder().assignedEmployee(nullEmployee).build();
        List<AnimalTask> animalTasks = animalTaskRepository.findFilteredTasks(EmployeeType.DOCTOR, searchTask);
        assertEquals(animalTasks.size(), 1);
    }


    @Test
    public void eventSearch_allSearchParametersNullReturnAll() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Animal animal = animalRepository.save(animal_basic);

        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);
        task_assigned.setEvent(true);
        task_assigned2.setEvent(true);
        task_assigned3.setEvent(true);

        Task createdTask = taskRepository.save(task_assigned);
        AnimalTask ec1 = AnimalTask.builder()
            .id(createdTask.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        AnimalTask ec2 = AnimalTask.builder()
            .id(createdTask2.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        AnimalTask ec3 = AnimalTask.builder()
            .id(createdTask3.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec3);
        List<AnimalTask> animalTasks = animalTaskRepository.findFilteredEvents(Task.builder().build());
        assertEquals(animalTasks.size(), 3);

        task_assigned.setEvent(false);
        task_assigned2.setEvent(false);
        task_assigned3.setEvent(false);
    }

    @Test
    public void eventSearch_noEvents() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        userLoginRepository.save(doctor_login);
        employeeRepository.save(doctor);
        Animal animal = animalRepository.save(animal_basic);

        Employee caretaker = employeeRepository.findEmployeeByUsername(USERNAME_DOCTOR_EMPLOYEE);

        task_assigned.setAssignedEmployee(caretaker);

        Task createdTask = taskRepository.save(task_assigned);
        AnimalTask ec1 = AnimalTask.builder()
            .id(createdTask.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        AnimalTask ec2 = AnimalTask.builder()
            .id(createdTask2.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        AnimalTask ec3 = AnimalTask.builder()
            .id(createdTask3.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec3);
        List<AnimalTask> animalTasks = animalTaskRepository.findFilteredEvents(Task.builder().build());
        assertEquals(animalTasks.size(), 0);

    }

    @Test
    public void eventSearch_searchDescriptionAndTitle() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        userLoginRepository.save(doctor_login);
        employeeRepository.save(doctor);
        Animal animal = animalRepository.save(animal_basic);
        task_assigned.setEvent(true);
        task_assigned.setTitle("AAAAAAAABBAAAAA");
        task_assigned.setDescription("AAAAAAAABBAAAAA");
        task_assigned2.setEvent(true);
        task_assigned3.setEvent(true);

        Employee caretaker = employeeRepository.findEmployeeByUsername(USERNAME_DOCTOR_EMPLOYEE);

        task_assigned.setAssignedEmployee(caretaker);

        Task createdTask = taskRepository.save(task_assigned);
        AnimalTask ec1 = AnimalTask.builder()
            .id(createdTask.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec1);

        Task createdTask2 = taskRepository.save(task_assigned2);
        AnimalTask ec2 = AnimalTask.builder()
            .id(createdTask2.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec2);

        Task createdTask3 = taskRepository.save(task_assigned3);
        AnimalTask ec3 = AnimalTask.builder()
            .id(createdTask3.getId())
            .subject(animal_basic)
            .build();
        animalTaskRepository.save(ec3);
        Task searchTask = Task.builder().title("aaaa").description("bA").build();
        List<AnimalTask> animalTasks = animalTaskRepository.findFilteredEvents(searchTask);
        assertEquals(animalTasks.size(), 1);

        task_assigned.setEvent(false);
        task_assigned2.setEvent(false);
        task_assigned3.setEvent(false);
    }

}
