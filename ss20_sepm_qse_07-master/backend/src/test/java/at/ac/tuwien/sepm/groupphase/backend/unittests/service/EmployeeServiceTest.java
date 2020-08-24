package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.AlreadyExistsException;
import at.ac.tuwien.sepm.groupphase.backend.exception.IncorrectTypeException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFreeException;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmployeeServiceTest implements TestData {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    UserService userService;

    @MockBean
    EmployeeRepository employeeRepository;

    @MockBean
    AnimalTaskRepository animalTaskRepository;

    @MockBean
    TaskRepository taskRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    AnimalRepository animalRepository;

    private Employee animal_caretaker = Employee.builder()
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .name(NAME_ANIMAL_CARE_EMPLOYEE)
        .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .type(TYPE_ANIMAL_CARE_EMPLOYEE)
        .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
        .workTimeStart(TEST_LOCAL_TIME_START)
        .workTimeEnd(TEST_LOCAL_TIME_END)
        .build();

    private Employee doctor = Employee.builder()
        .username(USERNAME_DOCTOR_EMPLOYEE)
        .name(NAME_DOCTOR_EMPLOYEE)
        .birthday(BIRTHDAY_DOCTOR_EMPLOYEE)
        .type(TYPE_DOCTOR_EMPLOYEE)
        .email(EMAIL_DOCTOR_EMPLOYEE)
        .workTimeStart(TEST_LOCAL_TIME_START)
        .workTimeEnd(TEST_LOCAL_TIME_END)
        .build();

    private Employee janitor = Employee.builder()
        .username(USERNAME_JANITOR_EMPLOYEE)
        .name(NAME_JANITOR_EMPLOYEE)
        .birthday(BIRTHDAY_JANITOR_EMPLOYEE)
        .type(TYPE_JANITOR_EMPLOYEE)
        .email(EMAIL_JANITOR_EMPLOYEE)
        .workTimeStart(TEST_LOCAL_TIME_START)
        .workTimeEnd(TEST_LOCAL_TIME_END)
        .build();

    private Animal horse = Animal.builder()
        .id(ANIMAL_ID)
        .name(ANIMAL_NAME_HORSE)
        .description(ANIMAL_DESCRIPTION_FAST)
        .species(ANIMAL_SPECIES_ARABIAN)
        .publicInformation(ANIMAL_PUBLIC_INFORMATION_FAMOUS)
        .build();

    private UserLogin userAnimalCareEmployee= UserLogin.builder()
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .password("something6")
        .build();

    private Task task_not_assigned = Task.builder()
        .id(4L)
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.NOT_ASSIGNED)
        .priority(false)
        .build();

    private AnimalTask animalTask_not_assigned = AnimalTask.builder()
        .id(1L)
        .subject(horse)
        .task(task_not_assigned)
        .build();


    private Enclosure enclosureMinimal = Enclosure.builder()
        .name("Wolf Enclosure")
        .id(5L)
        .description(null)
        .publicInfo(null)
        .picture(null)
        .build();

    private EnclosureTask enclosureTask_not_assigned = EnclosureTask.builder()
        .id(1L)
        .subject(enclosureMinimal)
        .task(task_not_assigned)
        .build();

    @Test
    public void emptyRepository_whenFindAll_thenEmptyList() {
        Mockito.when(employeeRepository.findAll()).thenReturn(new LinkedList<>());
        List<Employee> employees = employeeService.getAll();
        assertEquals(0, employees.size());
    }

    @Test
    public void filledRepository_findByNameAndTypeNoMatching_thenThrowNotFoundException() {
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAll().withIgnoreNullValues().withIgnoreCase()
            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
            .withMatcher("type", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<Employee> example = Example.of(Employee.builder().name("nonExistent").type(EmployeeType.JANITOR).build(), customExampleMatcher);
        Mockito.when(employeeRepository.findAll(example)).thenReturn(new LinkedList<>());
        assertThrows(NotFoundException.class, () -> employeeService.findByNameAndType(Employee.builder().name("nonExistent").type(EmployeeType.JANITOR).build()));
    }

    @Test
    public void filledRepository_whenFindAll_thenAllEntries() {
        List<Employee> employees = new LinkedList<>();
        employees.add(janitor);
        employees.add(doctor);
        employees.add(animal_caretaker);
        Mockito.when(employeeRepository.findAll()).thenReturn(employees);
        List<Employee> returnedEmployees = employeeService.getAll();
        returnedEmployees.containsAll(employees);
        assertEquals(3, returnedEmployees.size());
    }

    @Test
    public void findEmployeeByUsername_returnsRightEmployee() throws Exception{
        Mockito.when(employeeRepository.findEmployeeByUsername(animal_caretaker.getUsername())).thenReturn(animal_caretaker);
        Employee employee = employeeService.findByUsername(animal_caretaker.getUsername());
        assertEquals(animal_caretaker.getUsername(),employee.getUsername());
    }

    @Test
    public void createExistingEmployee_throwsAlreadyExistsException() throws Exception{
        Mockito.when(employeeRepository.findEmployeeByUsername(animal_caretaker.getUsername())).thenReturn(animal_caretaker);
        assertThrows(AlreadyExistsException.class,()->{employeeService.createEmployee(animal_caretaker);});
    }

    @Test
    public void assignAnimalToEmployee(){
        Mockito.when(employeeRepository.findEmployeeByUsername(animal_caretaker.getUsername())).thenReturn(animal_caretaker);
        Employee employee = employeeService.findByUsername(animal_caretaker.getUsername());
        List<Animal> animalsWithEmployee = new LinkedList<>();
        animalsWithEmployee.add(horse);
        Mockito.when(employeeRepository.findEmployeeByUsername(animal_caretaker.getUsername())).thenReturn(animal_caretaker);
        employeeService.assignAnimal(employee.getUsername(), horse.getId());
        Mockito.when(animalRepository.findAllByCaretakers(employee)).thenReturn(animalsWithEmployee);
        List<Animal> returnedAnimals = employeeService.findAssignedAnimals(employee.getUsername());
        assertEquals(1, returnedAnimals.size());
    }

    @Test
    public void assignAlreadyAssignedAnimalToEmployee()throws Exception{
        Mockito.when(employeeRepository.findEmployeeByUsername(animal_caretaker.getUsername())).thenReturn(animal_caretaker);
        Employee employee = employeeService.findByUsername(animal_caretaker.getUsername());
        List<Animal> animalsWithEmployee = new LinkedList<>();
        animalsWithEmployee.add(horse);
        Mockito.when(animalRepository.findAllByCaretakers(employee)).thenReturn(animalsWithEmployee);
        assertThrows(AlreadyExistsException.class,()->{employeeService.assignAnimal(employee.getUsername(), horse.getId());});
    }

    @Test
    public void createNonExistentEmployeeValidDate_returnsCreatedEmployee() throws Exception{
        Mockito.when(employeeRepository.findEmployeeByUsername(animal_caretaker.getUsername())).thenReturn(null);
        Mockito.when(employeeRepository.save(animal_caretaker)).thenReturn(animal_caretaker);
        assertEquals(employeeService.createEmployee(animal_caretaker), animal_caretaker);
    }

    @Test
    public void deleteEmployee(){
        Mockito.when(employeeRepository.findEmployeeByUsername(animal_caretaker.getUsername())).thenReturn(animal_caretaker);
        userService.createNewUser(userAnimalCareEmployee);
        Employee employee = employeeService.findByUsername(animal_caretaker.getUsername());
        employeeService.deleteEmployeeByUsername(employee.getUsername());
        List<Employee> employees = employeeService.getAll();
        assertEquals(0, employees.size());
    }


    @Test
    public void givenNothing_whenSaveAnimal_thenFindAnimalById() {
        Animal animal = Animal.builder()
            .id(1L)
            .name(null)
            .description(null)
            .enclosure(null)
            .species(null)
            .publicInformation("famous")
            .build();

        animalRepository.save(animal);
        assertAll( () -> assertNotNull(animalRepository.findById(animal.getId())));
    }

    @Test
    public void canBeAssigned_DoctorWithTime(){
        Optional<AnimalTask> animalTask = Optional.of(animalTask_not_assigned);
        Mockito.when(animalTaskRepository.findById(Mockito.anyLong())).thenReturn(animalTask);
        assertTrue(employeeService.canBeAssignedToTask(doctor, task_not_assigned));
    }

    @Test
    public void canBeAssigned_AnimalCaretakerWithTimeAssignedToAnimal(){
        List<Animal> animals = new LinkedList<>();
        Animal animal = Animal.builder()
            .id(1L).build();
        animals.add(animal);
        animal_caretaker.setAssignedAnimals(animals);
        Mockito.when(employeeRepository.findEmployeeByUsername(Mockito.anyString())).thenReturn(animal_caretaker);
        Optional<AnimalTask> animalTask = Optional.of(animalTask_not_assigned);
        Mockito.when(animalTaskRepository.findById(Mockito.anyLong())).thenReturn(animalTask);
        assertTrue(employeeService.canBeAssignedToTask(animal_caretaker, task_not_assigned));
        animal_caretaker.setAssignedAnimals(null);
    }

    @Test
    public void canNotBeAssigned_AnimalCaretakerWithTimeNotAssignedToAnimal(){
        List<Animal> animals = new LinkedList<>();
        animal_caretaker.setAssignedAnimals(animals);
        Mockito.when(employeeRepository.findEmployeeByUsername(Mockito.anyString())).thenReturn(animal_caretaker);
        Optional<AnimalTask> animalTask = Optional.of(animalTask_not_assigned);
        Mockito.when(animalTaskRepository.findById(Mockito.anyLong())).thenReturn(animalTask);
        assertFalse(employeeService.canBeAssignedToTask(animal_caretaker, task_not_assigned));
        animal_caretaker.setAssignedAnimals(null);
    }

    @Test
    public void canNotBeAssignedToAnimalTask_Janitor(){
        Optional<AnimalTask> animalTask = Optional.of(animalTask_not_assigned);
        Mockito.when(animalTaskRepository.findById(Mockito.anyLong())).thenReturn(animalTask);
        Exception exception = assertThrows(IncorrectTypeException.class, () -> {
            employeeService.canBeAssignedToTask(janitor, task_not_assigned);
        });
    }

    @Test
    public void doesNotHavePermission_AnimalCaretakerNotAssignedToAnimal(){
        List<Animal> animals = new LinkedList<>();
        animal_caretaker.setAssignedAnimals(animals);
        Optional<Employee> employee = Optional.of(animal_caretaker);
        Mockito.when(employeeRepository.findById(Mockito.anyString())).thenReturn(employee);
        Mockito.when(employeeRepository.findEmployeeByUsername(Mockito.anyString())).thenReturn(animal_caretaker);
        Optional<Task> task = Optional.of(task_not_assigned);
        Mockito.when(taskRepository.findById(Mockito.anyLong())).thenReturn(task);
        Optional<AnimalTask> animalTask = Optional.of(animalTask_not_assigned);
        Mockito.when(animalTaskRepository.findById(Mockito.anyLong())).thenReturn(animalTask);
        assertFalse(employeeService.hasTaskAssignmentPermissions(animal_caretaker.getUsername(), 1L));
        animal_caretaker.setAssignedAnimals(null);
    }

    @Test
    public void doesHavePermission_AnimalCaretakerAssignedToAnimal(){
        List<Animal> animals = new LinkedList<>();
        Animal animal = Animal.builder()
            .id(1L).build();
        animals.add(animal);
        animal_caretaker.setAssignedAnimals(animals);
        Optional<Employee> employee = Optional.of(animal_caretaker);
        Mockito.when(employeeRepository.findById(Mockito.anyString())).thenReturn(employee);
        Mockito.when(employeeRepository.findEmployeeByUsername(Mockito.anyString())).thenReturn(animal_caretaker);
        Optional<Task> task = Optional.of(task_not_assigned);
        Mockito.when(taskRepository.findById(Mockito.anyLong())).thenReturn(task);
        Optional<AnimalTask> animalTask = Optional.of(animalTask_not_assigned);
        Mockito.when(animalTaskRepository.findById(Mockito.anyLong())).thenReturn(animalTask);
        assertTrue(employeeService.hasTaskAssignmentPermissions(animal_caretaker.getUsername(), 1L));
        animal_caretaker.setAssignedAnimals(null);
    }

    @Test
    public void employeeIsFree_whenNoOtherTaskAssigned(){
        List<Task> taskList = new LinkedList<>();
        Mockito.when(taskRepository.findAllByAssignedEmployeeOrderByStartTime(Mockito.any(Employee.class))).thenReturn(taskList);
        assertTrue(employeeService.employeeIsFreeBetweenStartingAndEndtime(animal_caretaker, task_not_assigned));
    }

    @Test
    public void employeeIsNotFree_whenOtherTaskStartsBeforeThisTaskEnds(){
        Task task = Task.builder()
            .id(1L)
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_END_TIME.minusSeconds(5))
            .endTime(TAST_END_TIME.plusHours(2))
            .status(TaskStatus.NOT_ASSIGNED)
            .build();
        List<Task> taskList = new LinkedList<>();
        taskList.add(task);
        Mockito.when(taskRepository.findAllByAssignedEmployeeOrderByStartTime(Mockito.any(Employee.class))).thenReturn(taskList);
        Exception exception = assertThrows(NotFreeException.class, () -> {
            employeeService.employeeIsFreeBetweenStartingAndEndtime(animal_caretaker, task_not_assigned);
        });
    }

    @Test
    public void employeeIsNotFree_whenOtherTaskEndsAfterThisTaskStarts_BeforeThisTaskEnds(){
        Task task = Task.builder()
            .id(1L)
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME.minusHours(5))
            .endTime(TAST_START_TIME.plusSeconds(5))
            .status(TaskStatus.NOT_ASSIGNED)
            .build();
        List<Task> taskList = new LinkedList<>();
        taskList.add(task);
        Mockito.when(taskRepository.findAllByAssignedEmployeeOrderByStartTime(Mockito.any(Employee.class))).thenReturn(taskList);
        Exception exception = assertThrows(NotFreeException.class, () -> {
            employeeService.employeeIsFreeBetweenStartingAndEndtime(animal_caretaker, task_not_assigned);
        });
    }

    @Test
    public void employeeIsNotFree_whenThisTaskDuringOtherTask(){
        Task task = Task.builder()
            .id(1L)
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME.minusSeconds(5))
            .endTime(TAST_END_TIME.plusSeconds(5))
            .status(TaskStatus.NOT_ASSIGNED)
            .build();
        List<Task> taskList = new LinkedList<>();
        taskList.add(task);
        Mockito.when(taskRepository.findAllByAssignedEmployeeOrderByStartTime(Mockito.any(Employee.class))).thenReturn(taskList);
        Exception exception = assertThrows(NotFreeException.class, () -> {
            employeeService.employeeIsFreeBetweenStartingAndEndtime(animal_caretaker, task_not_assigned);
        });
    }

    @Test
    public void employeeIsNotFree_whenOtherTaskDuringThisTask(){
        Task task = Task.builder()
            .id(1L)
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME.plusSeconds(5))
            .endTime(TAST_END_TIME.minusSeconds(5))
            .status(TaskStatus.NOT_ASSIGNED)
            .build();
        List<Task> taskList = new LinkedList<>();
        taskList.add(task);
        Mockito.when(taskRepository.findAllByAssignedEmployeeOrderByStartTime(Mockito.any(Employee.class))).thenReturn(taskList);
        Exception exception = assertThrows(NotFreeException.class, () -> {
            employeeService.employeeIsFreeBetweenStartingAndEndtime(animal_caretaker, task_not_assigned);
        });
    }

    @Test
    public void employeeForAnimalTaskNonPriority(){
        Task task = Task.builder()
            .id(1L)
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME.plusSeconds(5).plusYears(2))
            .endTime(TAST_END_TIME.minusSeconds(5).plusYears(2))
            .status(TaskStatus.NOT_ASSIGNED)
            .build();
        List<Task> taskList = new LinkedList<>();
        taskList.add(task);
        Mockito.when(taskRepository.findAllByAssignedEmployeeOrderByStartTime(Mockito.any(Employee.class))).thenReturn(taskList);
        List<Employee> employees = new LinkedList<>();
        employees.add(animal_caretaker);
        Mockito.when(employeeRepository.findByAssignedAnimalsContains(Mockito.any(Animal.class))).thenReturn(employees);
        assertDoesNotThrow( () -> employeeService.findEmployeeForAnimalTask(animalTask_not_assigned, EmployeeType.ANIMAL_CARE));
    }

    @Test
    public void employeeForAnimalTaskNonPriorityNoOneFree(){
        Task task = Task.builder()
            .id(1L)
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME.plusSeconds(5))
            .endTime(TAST_END_TIME.minusSeconds(5))
            .status(TaskStatus.NOT_ASSIGNED)
            .build();
        List<Task> taskList = new LinkedList<>();
        taskList.add(task);
        Mockito.when(taskRepository.findAllByAssignedEmployeeOrderByStartTime(Mockito.any(Employee.class))).thenReturn(taskList);
        List<Employee> employees = new LinkedList<>();
        employees.add(animal_caretaker);
        Mockito.when(employeeRepository.findByAssignedAnimalsContains(Mockito.any(Animal.class))).thenReturn(employees);

        assertThrows(NotFreeException.class, () -> employeeService.findEmployeeForAnimalTask(animalTask_not_assigned, EmployeeType.ANIMAL_CARE));
    }

    @Test
    public void employeeForAnimalTaskPriority(){
        Task task = Task.builder()
            .id(1L)
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME.plusSeconds(5).plusYears(2))
            .endTime(TAST_END_TIME.minusSeconds(5).plusYears(2))
            .status(TaskStatus.NOT_ASSIGNED)
            .build();
        List<Task> taskList = new LinkedList<>();
        taskList.add(task);
        Mockito.when(taskRepository.findAllByAssignedEmployeeOrderByStartTime(Mockito.any(Employee.class))).thenReturn(taskList);
        List<Employee> employees = new LinkedList<>();
        employees.add(animal_caretaker);
        Mockito.when(employeeRepository.findByAssignedAnimalsContains(Mockito.any(Animal.class))).thenReturn(employees);
        animalTask_not_assigned.getTask().setPriority(true);
        assertDoesNotThrow( () -> employeeService.findEmployeeForAnimalTask(animalTask_not_assigned, EmployeeType.ANIMAL_CARE));
        animalTask_not_assigned.getTask().setPriority(false);
    }

    @Test
    public void employeeForAnimalTaskPriorityNonFittingTime(){
        Task task = Task.builder()
            .id(1L)
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME.plusSeconds(5).plusYears(2))
            .endTime(TAST_END_TIME.minusSeconds(5).plusYears(2))
            .status(TaskStatus.NOT_ASSIGNED)
            .build();
        List<Task> taskList = new LinkedList<>();
        taskList.add(task);
        Mockito.when(taskRepository.findAllByAssignedEmployeeOrderByStartTime(Mockito.any(Employee.class))).thenReturn(taskList);
        List<Employee> employees = new LinkedList<>();
        employees.add(animal_caretaker);
        Mockito.when(employeeRepository.findByAssignedAnimalsContains(Mockito.any(Animal.class))).thenReturn(employees);
        animalTask_not_assigned.getTask().setPriority(true);
        LocalDateTime startTimeSaved = animalTask_not_assigned.getTask().getStartTime();
        LocalDateTime endTimeSave = animalTask_not_assigned.getTask().getEndTime();
        animalTask_not_assigned.getTask().setStartTime(LocalDateTime.of(2020, 9, 25, 0, 10));
        animalTask_not_assigned.getTask().setEndTime(LocalDateTime.of(2020, 9, 25, 23, 10));
        assertThrows(NotFreeException.class, () -> employeeService.findEmployeeForAnimalTask(animalTask_not_assigned, EmployeeType.ANIMAL_CARE));
        animalTask_not_assigned.getTask().setPriority(false);
        animalTask_not_assigned.getTask().setStartTime(startTimeSaved);
        animalTask_not_assigned.getTask().setEndTime(endTimeSave);
    }

    @Test
    public void employeeForEnclosureTaskNonPriority(){
        Task task = Task.builder()
            .id(1L)
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME.plusSeconds(5).plusYears(2))
            .endTime(TAST_END_TIME.minusSeconds(5).plusYears(2))
            .status(TaskStatus.NOT_ASSIGNED)
            .build();
        List<Task> taskList = new LinkedList<>();
        taskList.add(task);
        Mockito.when(taskRepository.findAllByAssignedEmployeeOrderByStartTime(Mockito.any(Employee.class))).thenReturn(taskList);
        List<Employee> employees = new LinkedList<>();
        employees.add(animal_caretaker);
        Mockito.when(employeeRepository.getEmployeesByEnclosureID(Mockito.any(Long.class))).thenReturn(employees);
        assertDoesNotThrow( () -> employeeService.findEmployeeForEnclosureTask(enclosureTask_not_assigned, EmployeeType.ANIMAL_CARE));
    }

    @Test
    public void employeeForEnclosureTaskNonPriorityNoOneFree(){
        Task task = Task.builder()
            .id(1L)
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME.plusSeconds(5))
            .endTime(TAST_END_TIME.minusSeconds(5))
            .status(TaskStatus.NOT_ASSIGNED)
            .build();
        List<Task> taskList = new LinkedList<>();
        taskList.add(task);
        Mockito.when(taskRepository.findAllByAssignedEmployeeOrderByStartTime(Mockito.any(Employee.class))).thenReturn(taskList);
        List<Employee> employees = new LinkedList<>();
        employees.add(animal_caretaker);
        Mockito.when(employeeRepository.getEmployeesByEnclosureID(Mockito.any(Long.class))).thenReturn(employees);
        assertThrows(NotFreeException.class, () -> employeeService.findEmployeeForEnclosureTask(enclosureTask_not_assigned, EmployeeType.ANIMAL_CARE));
    }

    @Test
    public void employeeForEnclosureTaskPriority(){
        Task task = Task.builder()
            .id(1L)
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME.plusSeconds(5).plusYears(2))
            .endTime(TAST_END_TIME.minusSeconds(5).plusYears(2))
            .status(TaskStatus.NOT_ASSIGNED)
            .build();
        List<Task> taskList = new LinkedList<>();
        taskList.add(task);
        Mockito.when(taskRepository.findAllByAssignedEmployeeOrderByStartTime(Mockito.any(Employee.class))).thenReturn(taskList);
        List<Employee> employees = new LinkedList<>();
        employees.add(animal_caretaker);
        Mockito.when(employeeRepository.getEmployeesByEnclosureID(Mockito.any(Long.class))).thenReturn(employees);
        enclosureTask_not_assigned.getTask().setPriority(true);
        assertDoesNotThrow( () -> employeeService.findEmployeeForEnclosureTask(enclosureTask_not_assigned, EmployeeType.ANIMAL_CARE));
        enclosureTask_not_assigned.getTask().setPriority(false);
    }

    @Test
    public void employeeForEnclosureTaskPriorityNonFittingTask(){
        Task task = Task.builder()
            .id(1L)
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME.plusSeconds(5).plusYears(2))
            .endTime(TAST_END_TIME.minusSeconds(5).plusYears(2))
            .status(TaskStatus.NOT_ASSIGNED)
            .build();
        List<Task> taskList = new LinkedList<>();
        taskList.add(task);
        Mockito.when(taskRepository.findAllByAssignedEmployeeOrderByStartTime(Mockito.any(Employee.class))).thenReturn(taskList);
        List<Employee> employees = new LinkedList<>();
        employees.add(animal_caretaker);
        Mockito.when(employeeRepository.getEmployeesByEnclosureID(Mockito.any(Long.class))).thenReturn(employees);
        enclosureTask_not_assigned.getTask().setPriority(true);
        LocalDateTime startTimeSaved = enclosureTask_not_assigned.getTask().getStartTime();
        LocalDateTime endTimeSave = enclosureTask_not_assigned.getTask().getEndTime();
        enclosureTask_not_assigned.getTask().setStartTime(LocalDateTime.of(2020, 9, 25, 0, 10));
        enclosureTask_not_assigned.getTask().setEndTime(LocalDateTime.of(2020, 9, 25, 23, 10));
        assertThrows(NotFreeException.class, () -> employeeService.findEmployeeForEnclosureTask(enclosureTask_not_assigned, EmployeeType.ANIMAL_CARE));
        enclosureTask_not_assigned.getTask().setPriority(false);
        animalTask_not_assigned.getTask().setStartTime(startTimeSaved);
        animalTask_not_assigned.getTask().setEndTime(endTimeSave);
    }

}