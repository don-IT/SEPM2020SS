package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.IncorrectTypeException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.aspectj.weaver.ast.Not;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.time.temporal.ChronoUnit;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class RepeatableTaskServiceTest implements TestData {

    @Autowired
    TaskService taskService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    AnimalTaskRepository animalTaskRepository;

    @Autowired
    EnclosureTaskRepository enclosureTaskRepository;

    @Autowired
    RepeatableTaskRepository repeatableTaskRepository;

    @Autowired
    AnimalRepository animalRepository;

    @Autowired
    EnclosureRepository enclosureRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    AnimalService animalService;

    Animal animal = Animal.builder()
        .id(2L)
        .name("Brandy")
        .description("racing Horce")
        .enclosure(null)
        .species("race")
        .publicInformation(null)
        .build();

    private Employee anmial_caretaker = Employee.builder()
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .name(NAME_ANIMAL_CARE_EMPLOYEE)
        .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .type(TYPE_ANIMAL_CARE_EMPLOYEE)
        .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
        .workTimeStart(TEST_LOCAL_TIME_START)
        .workTimeEnd(TEST_LOCAL_TIME_END)
        .build();

    private Task task_assigned = Task.builder()
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .priority(false)
        .build();

    private Enclosure enclosureDetailed = Enclosure.builder()
        .name(NAME_LION_ENCLOSURE)
        .description(DESCRIPTION_LION_ENCLOSURE)
        .publicInfo(PUBLIC_INFO_LION_ENCLOSURE)
        .picture(PICTURE_LION_ENCLOSURE)
        .build();

    private Task task = Task.builder()
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .build();

    private Enclosure enclosure;

    @BeforeEach
    public void beforeEach() {
        enclosureTaskRepository.deleteAll();
        animalTaskRepository.deleteAll();
        repeatableTaskRepository.deleteAll();
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
        animalRepository.deleteAll();
        enclosureRepository.deleteAll();
        enclosure = enclosureRepository.save(enclosureDetailed);
        animal.setId(animalRepository.save(animal).getId());
        animalService.addAnimalToEnclosure(animal, enclosure.getId());
        task_assigned.setAssignedEmployee(employeeRepository.save(anmial_caretaker));
        employeeService.assignAnimal(anmial_caretaker.getUsername(), animal.getId());
    }

    @AfterEach
    public void afterEach() {
        enclosureTaskRepository.deleteAll();
        animalTaskRepository.deleteAll();
        repeatableTaskRepository.deleteAll();
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
        animalRepository.deleteAll();
        enclosureRepository.deleteAll();
        animal.setId(2L);
        task_assigned.setAssignedEmployee(null);
        enclosureDetailed.setId(null);
    }

    @Test
    public void creatingRepeatableAnimalTasks_thenTasksInRepository() {
        List<AnimalTask> animalTaskList = taskService.createRepeatableAnimalTask(task_assigned, animal, 4, ChronoUnit.DAYS, 2);

        RepeatableTask firstTaskRepeatable = repeatableTaskRepository.findById(animalTaskList.get(3).getId()).get();
        Task firstTask = firstTaskRepeatable.getTask();
        Task secondTask = firstTaskRepeatable.getFollowTask();

        assertNotNull(firstTaskRepeatable);
        assertEquals(4, taskRepository.findAll().size());
        assertEquals(4, animalTaskRepository.findAll().size());
        assertEquals(4, repeatableTaskRepository.findAll().size());
        assertEquals(firstTask.getStartTime().plus(2, ChronoUnit.DAYS), secondTask.getStartTime());
    }

    @Test
    public void creatingRepeatableEnclosureTasks_thenTasksInRepository() {
        List<EnclosureTask> enclosureTaskList = taskService.createRepeatableEnclosureTask(task_assigned, enclosureDetailed, 4, ChronoUnit.DAYS, 2);

        RepeatableTask firstTaskRepeatable = repeatableTaskRepository.findById(enclosureTaskList.get(3).getId()).get();
        Task firstTask = firstTaskRepeatable.getTask();
        Task secondTask = firstTaskRepeatable.getFollowTask();

        assertNotNull(firstTaskRepeatable);
        assertEquals(4, taskRepository.findAll().size());
        assertEquals(4, enclosureTaskRepository.findAll().size());
        assertEquals(4, repeatableTaskRepository.findAll().size());
        assertEquals(firstTask.getStartTime().plus(2, ChronoUnit.DAYS), secondTask.getStartTime());
    }

    @Test
    public void deleteRepeatableTask_thenCorrectFollowingTask() {
        List<AnimalTask> animalTaskList = taskService.createRepeatableAnimalTask(task_assigned, animal, 3, ChronoUnit.DAYS, 2);

        RepeatableTask firstTaskRepeatable = repeatableTaskRepository.findById(animalTaskList.get(2).getId()).get();
        Task firstTask = firstTaskRepeatable.getTask();
        Task secondTask = firstTaskRepeatable.getFollowTask();
        RepeatableTask secondTaskRepeatable = repeatableTaskRepository.findById(secondTask.getId()).get();
        Task thirdTask = secondTaskRepeatable.getFollowTask();

        taskService.deleteTask(secondTask.getId());

        firstTaskRepeatable = repeatableTaskRepository.findById(firstTask.getId()).get();

        assertEquals(thirdTask.getId(), firstTaskRepeatable.getFollowTask().getId());
    }

    @Test
    public void repeatDeleteFirstTask_thenAllTasksDeleted() {
        List<AnimalTask> animalTaskList = taskService.createRepeatableAnimalTask(task_assigned, animal, 4, ChronoUnit.DAYS, 2);

        RepeatableTask firstTaskRepeatable = repeatableTaskRepository.findById(animalTaskList.get(3).getId()).get();

        taskService.repeatDeleteTask(firstTaskRepeatable.getId());

        assertTrue(taskRepository.findAll().isEmpty());
    }

    @Test
    public void creatingRepeatableTask_whenHighPriority_IncorrectTypeException() {
        task_assigned.setPriority(true);

        assertThrows(IncorrectTypeException.class, () -> taskService.createRepeatableAnimalTask(task_assigned, animal, 4, ChronoUnit.DAYS, 2));

        task_assigned.setPriority(false);
    }

    @Test
    public void editingRepeatableAnimalTask_thenNewValuesSaved() {
        List<AnimalTask> animalTaskList = taskService.createRepeatableAnimalTask(task_assigned, animal, 4, ChronoUnit.DAYS, 2);

        Animal animal2 = Animal.builder()
            .name("Brandy2")
            .description("racing Horce")
            .enclosure(null)
            .species("race")
            .publicInformation(null)
            .build();

        Animal newAnimal = animalRepository.save(animal2);

        RepeatableTask firstTaskRepeatable = repeatableTaskRepository.findById(animalTaskList.get(3).getId()).get();

        Task newTask = Task.builder().priority(true).title("alternate Title").description("new description").id(firstTaskRepeatable.getId()).build();
        AnimalTask newAnimalTask = AnimalTask.builder().id(firstTaskRepeatable.getId()).task(newTask).subject(newAnimal).build();

        taskService.repeatUpdateAnimalTaskInformation(newAnimalTask);

        List<AnimalTask> animalTasks = animalTaskRepository.findAll();

        assertEquals(4, animalTaskRepository.findAll().size());
        for(AnimalTask a : animalTasks) {
            assertTrue(a.getTask().isPriority());
            assertEquals("alternate Title", a.getTask().getTitle());
            assertEquals("new description", a.getTask().getDescription());
            assertEquals(newAnimal.getId(), a.getSubject().getId());
        }
    }

    @Test
    public void editingRepeatableAnimalTask_whenIdNotExisting_thenNotFoundException() {
        List<AnimalTask> animalTaskList = taskService.createRepeatableAnimalTask(task_assigned, animal, 1, ChronoUnit.DAYS, 2);

        RepeatableTask firstTaskRepeatable = repeatableTaskRepository.findById(animalTaskList.get(0).getId()).get();

        assertThrows(NotFoundException.class, () -> taskService.repeatUpdateAnimalTaskInformation(AnimalTask.builder().id(firstTaskRepeatable.getId() + 1).subject(new Animal()).build()));
    }

    @Test
    public void editingRepeatableEnclosureTask_thenNewValuesSaved() {
        List<EnclosureTask> enclosureTaskList = taskService.createRepeatableEnclosureTask(task_assigned, enclosure, 4, ChronoUnit.DAYS, 2);

        Enclosure enclosure2 = enclosureRepository.save(Enclosure.builder()
            .name(NAME_LION_ENCLOSURE)
            .description(DESCRIPTION_LION_ENCLOSURE)
            .publicInfo(PUBLIC_INFO_LION_ENCLOSURE)
            .picture(PICTURE_LION_ENCLOSURE)
            .build());

        RepeatableTask firstTaskRepeatable = repeatableTaskRepository.findById(enclosureTaskList.get(3).getId()).get();

        Task newTask = Task.builder().priority(true).title("alternate Title").description("new description").id(firstTaskRepeatable.getId()).build();
        EnclosureTask newEnclosureTask = EnclosureTask.builder().id(firstTaskRepeatable.getId()).task(newTask).subject(enclosure2).build();

        taskService.repeatUpdateEnclosureTaskInformation(newEnclosureTask);

        List<EnclosureTask> enclosureTasks = enclosureTaskRepository.findAll();

        assertEquals(4, enclosureTaskRepository.findAll().size());
        for(EnclosureTask e : enclosureTasks) {
            assertTrue(e.getTask().isPriority());
            assertEquals("alternate Title", e.getTask().getTitle());
            assertEquals("new description", e.getTask().getDescription());
            assertEquals(enclosure2.getId(), e.getSubject().getId());
        }
    }

    @Test
    public void editingRepeatableEnclosureTask_whenIdNotExisting_thenNotFoundException() {
        List<EnclosureTask> enclosureTaskList = taskService.createRepeatableEnclosureTask(task_assigned, enclosure, 1, ChronoUnit.DAYS, 2);

        RepeatableTask firstTaskRepeatable = repeatableTaskRepository.findById(enclosureTaskList.get(0).getId()).get();

        assertThrows(NotFoundException.class, () -> taskService.repeatUpdateEnclosureTaskInformation(EnclosureTask.builder().id(firstTaskRepeatable.getId() + 1).subject(new Enclosure()).build()));
    }

    @Test
    public void automaticallyAssignAnimalTaskRepeat_whenEmployeeAvailable_thenAssigned() {
        anmial_caretaker.setType(EmployeeType.DOCTOR);
        Employee employee = employeeRepository.save(anmial_caretaker);
        anmial_caretaker.setType(TYPE_ANIMAL_CARE_EMPLOYEE);

        List<AnimalTask> animalTaskList = taskService.createRepeatableAnimalTask(task, animal, 4, ChronoUnit.DAYS, 2);

        RepeatableTask firstTaskRepeatable = repeatableTaskRepository.findById(animalTaskList.get(3).getId()).get();
        Task firstTask = firstTaskRepeatable.getTask();

        taskService.automaticallyAssignAnimalTaskRepeat(firstTask.getId(), EmployeeType.DOCTOR);

        List<Task> tasks = taskRepository.findAll();

        for(Task t : tasks) {
            assertEquals(TaskStatus.ASSIGNED, t.getStatus());
            assertEquals(employee.getUsername(), t.getAssignedEmployee().getUsername());
        }
    }

    @Test
    public void automaticallyAssignEnclosureTaskRepeat_whenEmployeeAvailable_thenAssigned() {
        anmial_caretaker.setType(EmployeeType.JANITOR);
        Employee employee = employeeRepository.save(anmial_caretaker);
        anmial_caretaker.setType(TYPE_ANIMAL_CARE_EMPLOYEE);

        List<EnclosureTask> enclosureTasks = taskService.createRepeatableEnclosureTask(task, enclosure, 4, ChronoUnit.DAYS, 2);

        RepeatableTask firstTaskRepeatable = repeatableTaskRepository.findById(enclosureTasks.get(3).getId()).get();
        Task firstTask = firstTaskRepeatable.getTask();

        taskService.automaticallyAssignEnclosureTaskRepeat(firstTask.getId(), EmployeeType.JANITOR);

        List<Task> tasks = taskRepository.findAll();

        for(Task t : tasks) {
            assertEquals(TaskStatus.ASSIGNED, t.getStatus());
            assertEquals(employee.getUsername(), t.getAssignedEmployee().getUsername());
        }
    }
}
