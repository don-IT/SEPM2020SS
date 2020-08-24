package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserLoginRepository;
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
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class TaskRepositoryTest implements TestData {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    UserLoginRepository userLoginRepository;


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

    private Task event_not_assigned = Task.builder()
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.NOT_ASSIGNED)
        .event(true)
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


    @BeforeEach
    public void beforeEach() {
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
    }

    @Test
    public void givenNothing_whenSaveTask_thenFindTaskById() {
        taskRepository.save(task_not_assigned);
        Task searchTask = taskRepository.findAll().get(0);
        assertNotNull(taskRepository.findById(searchTask.getId()));
    }

    @Test
    public void givenNothing_whenSaveEvent_thenFindEventById() {
        taskRepository.save(event_not_assigned);
        Task searchTask = taskRepository.findAll().get(0);
        assertNotNull(taskRepository.findEventById(searchTask.getId()));
    }

    @Test
    public void givenNothing_searchingForTasksOfEmployee_thenFindAllTasks() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);
        task_assigned2.setAssignedEmployee(caretaker);
        task_assigned3.setAssignedEmployee(caretaker);

        taskRepository.save(task_assigned);  //all 3 get  saved as one row if same object for some reason
        taskRepository.save(task_assigned2);
        taskRepository.save(task_assigned3);

        List<Task> searchTask = taskRepository.findAllByAssignedEmployeeOrderByStartTime(caretaker);
        assertEquals(searchTask.size(), 3);
    }

    @Test
    public void givenNothing_searchingForEventsOfEmployee_thenFindAllTasks() {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Employee caretaker = employeeRepository.findAll().get(0);

        task_assigned.setAssignedEmployee(caretaker);
        task_assigned2.setAssignedEmployee(caretaker);
        task_assigned3.setAssignedEmployee(caretaker);

        taskRepository.save(event_assigned);  //all 2 get  saved as one row if same object for some reason
        taskRepository.save(event_assigned2);

        List<Task> searchTask = taskRepository.findAllEventsByAssignedEmployeeOrderByStartTime(caretaker);
        assertEquals(searchTask.size(), 2);
    }

/*    @Test
    public void searchForTasksOfEmployee*/


}
