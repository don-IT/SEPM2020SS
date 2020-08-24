package at.ac.tuwien.sepm.groupphase.backend.integrationtest.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EmployeeMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EnclosureTaskMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TaskEndpointTest implements TestData {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AnimalTaskRepository animalTaskRepository;

    @Autowired
    private EnclosureTaskRepository enclosureTaskRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    //for testing assignment
    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private EnclosureRepository enclosureRepository;

    @Autowired
    private RepeatableTaskRepository repeatableTaskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private AnimalMapper animalMapper;

    @Autowired
    private EnclosureTaskMapper enclosureTaskMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    String ANIMAL_TASK_CREATION_BASE_URI = TASK_BASE_URI + "/animal";
    String ENCLOSURE_TASK_CREATION_BASE_URI = TASK_BASE_URI + "/enclosure";

    String ANIMAL_TASK_GET_BY_EMPLOYEE_BASE_URI = TASK_BASE_URI + "/employee";


    private final UserLogin admin_login = UserLogin.builder()
        .isAdmin(true)
        .username(ADMIN_USER)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

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

    private UserLogin doctor_login = UserLogin.builder()
        .isAdmin(false)
        .username(USERNAME_DOCTOR_EMPLOYEE)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
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

    private final UserLogin janitor_login = UserLogin.builder()
        .isAdmin(false)
        .username(USERNAME_JANITOR_EMPLOYEE)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private final Employee janitor = Employee.builder()
        .username(USERNAME_JANITOR_EMPLOYEE)
        .name(NAME_JANITOR_EMPLOYEE)
        .birthday(BIRTHDAY_JANITOR_EMPLOYEE)
        .type(TYPE_JANITOR_EMPLOYEE)
        .email(EMAIL_JANITOR_EMPLOYEE)
        .workTimeStart(TEST_LOCAL_TIME_START)
        .workTimeEnd(TEST_LOCAL_TIME_END)
        .build();

    private final Enclosure barn = Enclosure.builder().name("Barn").build();

    private final Animal animal = Animal.builder()
        .name("Horse")
        .description("Fast")
        .species("race")
        .publicInformation("famous")
        .build();

    private TaskDto taskDto = TaskDto.builder()
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .build();

    private RepeatableTaskDto repeatableTaskDto = RepeatableTaskDto.builder()
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .amount(3)
        .separation(ChronoUnit.WEEKS)
        .separationAmount(2)
        .build();

    private Task task = Task.builder()
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.ASSIGNED)
        .build();

    @BeforeEach
    public void beforeEach() {
        repeatableTaskRepository.deleteAll();
        animalTaskRepository.deleteAll();
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
        animalRepository.deleteAll();
        enclosureRepository.deleteAll();

        taskDto = TaskDto.builder()
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME)
            .endTime(TAST_END_TIME)
            .build();

        task = Task.builder()
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME)
            .endTime(TAST_END_TIME)
            .status(TaskStatus.ASSIGNED)
            .build();

    }

    @AfterEach
    public void afterEach() {
        repeatableTaskRepository.deleteAll();
        animalTaskRepository.deleteAll();
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
        animalRepository.deleteAll();
        enclosureRepository.deleteAll();
    }

    @Test
    public void validAnimalTask_createdByAdmin_returnsExpectedAnimalTaskDto() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animal);
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);
        taskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        AnimalTaskDto messageResponse = objectMapper.readValue(response.getContentAsString(),
            AnimalTaskDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertEquals(taskDto.getTitle(), messageResponse.getTitle()),
            () -> assertEquals(taskDto.getDescription(), messageResponse.getDescription()),
            () -> assertEquals(taskDto.getAssignedEmployeeUsername(), messageResponse.getAssignedEmployeeUsername()),
            () -> assertEquals(taskDto.getStartTime(), messageResponse.getStartTime()),
            () -> assertEquals(taskDto.getEndTime(), messageResponse.getEndTime()),
            () -> assertEquals(messageResponse.getStatus(), TaskStatus.ASSIGNED),
            () -> assertEquals(animal.getName(), messageResponse.getAnimalName())
        );
    }

    @Test
    public void invalidTimeAnimalTask_createdByAdmin_returnsBadRequest() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animal);
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);
        taskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        LocalDateTime endTime = taskDto.getEndTime();
        taskDto.setEndTime(taskDto.getStartTime());
        taskDto.setStartTime(endTime);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

 /*  @Test
    public void validAnimalTaskButInvalidAssignedWorker_createdByAdmin_returnsUnprocessableEntity() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(janitor);
        taskDto.setAssignedEmployeeUsername(USERNAME_JANITOR_EMPLOYEE);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    } */

    @Test
    public void validTaskButAnimalDoesNotExist_createdByAdmin_returnsNotFound() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        taskDto.setAssignedEmployeeUsername(USERNAME_JANITOR_EMPLOYEE);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + (savedAnimal.getId() + 2))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void validTaskButEmployeeNotFree_createdByAdmin_returnsConflict() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animal);
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);
        taskRepository.save(Task.builder().assignedEmployee(anmial_caretaker).status(TaskStatus.ASSIGNED).title(TASK_TITLE).description(TASK_DESCRIPTION).startTime(TAST_START_TIME).endTime(TAST_END_TIME).build());
        taskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    @Test
    public void validAnimalTaskNoGivenUsername_createdByAdmin_returnsNotAssignedAnimalTaskDto() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);

        taskDto.setAssignedEmployeeUsername(null);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        AnimalTaskDto messageResponse = objectMapper.readValue(response.getContentAsString(),
            AnimalTaskDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertEquals(taskDto.getTitle(), messageResponse.getTitle()),
            () -> assertEquals(taskDto.getDescription(), messageResponse.getDescription()),
            () -> assertNull(messageResponse.getAssignedEmployeeUsername()),
            () -> assertEquals(taskDto.getStartTime(), messageResponse.getStartTime()),
            () -> assertEquals(taskDto.getEndTime(), messageResponse.getEndTime()),
            () -> assertEquals(messageResponse.getStatus(), TaskStatus.NOT_ASSIGNED),
            () -> assertEquals(animal.getName(), messageResponse.getAnimalName())
        );
    }


    @Test
    public void invalidAnimalTaskUpdate_employeeDoesNotFulfillAssignmentCriteria_returnsUnprocessableEntity() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);

        task.setAssignedEmployee(null);
        task.setStatus(TaskStatus.NOT_ASSIGNED);
        taskRepository.save(task);
        task = taskRepository.findAll().get(0);
        AnimalTask animalTask = AnimalTask.builder().subject(animalRepository.findAll().get(0)).id(task.getId()).build();
        animalTaskRepository.save(animalTask);

        EmployeeDto employeeDto = EmployeeDto.builder().username(USERNAME_ANIMAL_CARE_EMPLOYEE).build();
        String body = objectMapper.writeValueAsString(employeeDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/" + task.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void invalidAnimalTaskUpdate_alreadyAssigned_returnsUnprocessableEntity() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animalRepository.findAll().get(0));
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);

        task.setAssignedEmployee(null);
        task.setStatus(TaskStatus.ASSIGNED);
        taskRepository.save(task);
        task = taskRepository.findAll().get(0);
        AnimalTask animalTask = AnimalTask.builder().subject(animalRepository.findAll().get(0)).id(task.getId()).build();
        animalTaskRepository.save(animalTask);

        EmployeeDto employeeDto = EmployeeDto.builder().username(USERNAME_ANIMAL_CARE_EMPLOYEE).build();
        String body = objectMapper.writeValueAsString(employeeDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/" + task.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }

    @Test
    public void invalidAnimalTaskUpdate_taskIdDoesntExist_returnsNotFound() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animalRepository.findAll().get(0));
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);

        task.setAssignedEmployee(null);
        task.setStatus(TaskStatus.NOT_ASSIGNED);
        taskRepository.save(task);
        task = taskRepository.findAll().get(0);
        AnimalTask animalTask = AnimalTask.builder().subject(animalRepository.findAll().get(0)).id(task.getId()).build();
        animalTaskRepository.save(animalTask);

        EmployeeDto employeeDto = EmployeeDto.builder().username(USERNAME_ANIMAL_CARE_EMPLOYEE).build();
        String body = objectMapper.writeValueAsString(employeeDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/" + (task.getId() + 1))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );
    }

    @Test
    public void validAnimalTaskUpdate_returnsOk() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animalRepository.findAll().get(0));
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);

        task.setAssignedEmployee(null);
        task.setStatus(TaskStatus.NOT_ASSIGNED);
        taskRepository.save(task);
        task = taskRepository.findAll().get(0);
        AnimalTask animalTask = AnimalTask.builder().subject(animalRepository.findAll().get(0)).id(task.getId()).build();
        animalTaskRepository.save(animalTask);

        EmployeeDto employeeDto = EmployeeDto.builder().username(USERNAME_ANIMAL_CARE_EMPLOYEE).build();
        String body = objectMapper.writeValueAsString(employeeDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/" + task.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );
    }

    @Test
    public void deleteTask_StatusOk() throws Exception {
        Animal savedAnimal = animalRepository.save(animal);
        Task savedTask = taskRepository.save(task);
        animalTaskRepository.save(AnimalTask.builder().id(savedTask.getId()).subject(savedAnimal).build());
        MvcResult mvcResult = this.mockMvc.perform(delete(TASK_BASE_URI + "/" + task.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void deleteTask_whenTaskIdNotExisting_StatusNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(TASK_BASE_URI + "/10")
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void validGetListOfAnimalTasksFromEmployee() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animalRepository.findAll().get(0));
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);

        task.setAssignedEmployee(anmial_caretaker);
        task.setStatus(TaskStatus.ASSIGNED);
        taskRepository.save(task);
        task = taskRepository.findAll().get(0);
        AnimalTask animalTask = AnimalTask.builder().subject(animalRepository.findAll().get(0)).id(task.getId()).build();
        animalTaskRepository.save(animalTask);


        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(ANIMAL_TASK_GET_BY_EMPLOYEE_BASE_URI + "/animal-task" + "/" + anmial_caretaker.getUsername())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<AnimalTaskDto> animalTaskDtos = objectMapper.readValue(response.getContentAsString(),
            new TypeReference<List<AnimalTaskDto>>() {
            });
        assertAll(
            () -> assertEquals(1, animalTaskDtos.size()),
            () -> assertEquals(animal.getName(), animalTaskDtos.get(0).getAnimalName()),
            () -> assertEquals(task.getTitle(), animalTaskDtos.get(0).getTitle()),
            () -> assertEquals(anmial_caretaker.getUsername(), animalTaskDtos.get(0).getAssignedEmployeeUsername()),
            () -> assertEquals(task.getDescription(), animalTaskDtos.get(0).getDescription())
        );

    }

    @Test
    public void validGetListOfAnimalTasksFromNonExistingEmployee_returnsNotFound() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animalRepository.findAll().get(0));
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);

        task.setAssignedEmployee(anmial_caretaker);
        task.setStatus(TaskStatus.ASSIGNED);
        taskRepository.save(task);
        task = taskRepository.findAll().get(0);
        AnimalTask animalTask = AnimalTask.builder().subject(animalRepository.findAll().get(0)).id(task.getId()).build();
        animalTaskRepository.save(animalTask);


        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(ANIMAL_TASK_GET_BY_EMPLOYEE_BASE_URI + "/" + "I_DONT_EXIST")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void validTaskUpdateStatusDoneAsAdmin_returnsOk() throws Exception {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);

        task.setAssignedEmployee(anmial_caretaker);
        task.setStatus(TaskStatus.ASSIGNED);
        taskRepository.save(task);
        task = taskRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/finished/" + task.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );
    }


    @Test
    public void validAnimalTaskButInvalidAssignedWorker_createdByAdmin_returnsUnprocessableEntity() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(janitor);
        taskDto.setAssignedEmployeeUsername(USERNAME_JANITOR_EMPLOYEE);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }


    @Test
    public void validTaskUpdateStatusDoneAsUser_returnsOk() throws Exception {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);

        task.setAssignedEmployee(anmial_caretaker);
        task.setStatus(TaskStatus.ASSIGNED);
        taskRepository.save(task);
        task = taskRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/finished/" + task.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(anmial_caretaker.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus())
        );
    }


    @Test
    public void validEnclosureTask_createdByAdmin_returnsExpectedEnclosureTaskDto() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animal);
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);
        taskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ENCLOSURE_TASK_CREATION_BASE_URI + "/" + enclosure.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        AnimalTaskDto messageResponse = objectMapper.readValue(response.getContentAsString(),
            AnimalTaskDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertEquals(taskDto.getTitle(), messageResponse.getTitle()),
            () -> assertEquals(taskDto.getDescription(), messageResponse.getDescription()),
            () -> assertEquals(taskDto.getAssignedEmployeeUsername(), messageResponse.getAssignedEmployeeUsername()),
            () -> assertEquals(taskDto.getStartTime(), messageResponse.getStartTime()),
            () -> assertEquals(taskDto.getEndTime(), messageResponse.getEndTime()),
            () -> assertEquals(messageResponse.getStatus(), TaskStatus.ASSIGNED)

        );
    }



    @Test
    public void validTaskUpdateStatusDoneAsWrongUser_returnsForbidden() throws Exception {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(janitor);

        task.setAssignedEmployee(anmial_caretaker);
        task.setStatus(TaskStatus.ASSIGNED);
        taskRepository.save(task);
        task = taskRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/finished/" + task.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(janitor.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus())
        );
    }

    @Test
    public void invalidTimeEnclosureTask_createdByAdmin_returnsBadRequest() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        taskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        LocalDateTime endTime = taskDto.getEndTime();
        taskDto.setEndTime(taskDto.getStartTime());
        taskDto.setStartTime(endTime);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ENCLOSURE_TASK_CREATION_BASE_URI + "/" + enclosure.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void updateTaskAsAdminValid_expectStatusCreated() throws Exception {
        String description = "New description";
        String title = "New title";
        LocalDateTime startTime = LocalDateTime.of(2020, 7, 12, 11, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 7, 12, 12, 0, 0);
        TaskStatus taskStatus = TaskStatus.NOT_ASSIGNED;

        Animal subject = animalRepository.save(animal);
        Animal subject2 = animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        userLoginRepository.save(doctor_login);
        employeeRepository.save(doctor);
        animalRepository.assignAnimalToCaretaker(anmial_caretaker.getUsername(),subject.getId());
        Task taskDef = taskRepository.save(task);

        AnimalTask animalTask = taskService.createAnimalTask(taskDef, subject);

        CombinedTaskDto combinedTaskDto = CombinedTaskDto.builder()
            .id(taskDef.getId()).title(title).description(description)
            .startTime(startTime).endTime(endTime).status(taskStatus)
            .animalTask(true).subjectId(subject2.getId())
            .assignedEmployeeUsername(doctor.getUsername())
            .priority(true).build();

        String body = objectMapper.writeValueAsString(combinedTaskDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        AnimalTask updatedTask = taskService.getAnimalTaskById(taskDef.getId());
        assertAll(
            () -> {assertEquals(updatedTask.getTask().getTitle(),title);},
            () -> {assertEquals(updatedTask.getTask().getDescription(),description);},
            () -> {assertEquals(updatedTask.getTask().getStartTime(),startTime);},
            () -> {assertEquals(updatedTask.getTask().getEndTime(),endTime);},
            () -> {assertEquals(updatedTask.getTask().getStatus(),TaskStatus.ASSIGNED);},
            () -> {assertEquals(updatedTask.getTask().isPriority(),true);},
            () -> {assertEquals(updatedTask.getTask().getAssignedEmployee().getUsername(),doctor.getUsername());},
            () -> {assertEquals(updatedTask.getSubject().getId(),subject2.getId());}
        );
    }


    @Test
    public void updateTaskAsUserValid_expectStatusCreated() throws Exception {
        String description = "New description";
        String title = "New title";
        LocalDateTime startTime = LocalDateTime.of(2020, 7, 12, 11, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 7, 12, 12, 0, 0);
        TaskStatus taskStatus = TaskStatus.NOT_ASSIGNED;

        Animal subject = animalRepository.save(animal);
        Animal subject2 = animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        userLoginRepository.save(doctor_login);
        employeeRepository.save(doctor);
        animalRepository.assignAnimalToCaretaker(anmial_caretaker.getUsername(),subject.getId());
        Task taskDef = taskRepository.save(task);

        AnimalTask animalTask = taskService.createAnimalTask(taskDef, subject);

        CombinedTaskDto combinedTaskDto = CombinedTaskDto.builder()
            .id(taskDef.getId()).title(title).description(description)
            .startTime(startTime).endTime(endTime).status(taskStatus)
            .animalTask(true).subjectId(subject2.getId())
            .assignedEmployeeUsername(doctor.getUsername())
            .priority(true).build();

        String body = objectMapper.writeValueAsString(combinedTaskDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(animal_caretaker_login.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        AnimalTask updatedTask = taskService.getAnimalTaskById(taskDef.getId());
        assertAll(
            () -> {assertEquals(updatedTask.getTask().getTitle(),title);},
            () -> {assertEquals(updatedTask.getTask().getDescription(),description);},
            () -> {assertEquals(updatedTask.getTask().getStartTime(),startTime);},
            () -> {assertEquals(updatedTask.getTask().getEndTime(),endTime);},
            () -> {assertEquals(updatedTask.getTask().getStatus(),TaskStatus.ASSIGNED);},
            () -> {assertEquals(updatedTask.getTask().isPriority(),true);},
            () -> {assertEquals(updatedTask.getTask().getAssignedEmployee().getUsername(),doctor.getUsername());},
            () -> {assertEquals(updatedTask.getSubject().getId(),subject2.getId());}
        );
    }

    @Test
    public void updateTaskAsDoctorValid_expectStatusForbidded() throws Exception {
        String description = "New description";
        String title = "New title";
        LocalDateTime startTime = LocalDateTime.of(2020, 7, 12, 11, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 7, 12, 12, 0, 0);
        TaskStatus taskStatus = TaskStatus.NOT_ASSIGNED;

        Animal subject = animalRepository.save(animal);
        Animal subject2 = animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        userLoginRepository.save(doctor_login);
        employeeRepository.save(doctor);
        animalRepository.assignAnimalToCaretaker(anmial_caretaker.getUsername(),subject.getId());
        Task taskDef = taskRepository.save(task);

        AnimalTask animalTask = taskService.createAnimalTask(taskDef, subject);

        CombinedTaskDto combinedTaskDto = CombinedTaskDto.builder()
            .id(taskDef.getId()).title(title).description(description)
            .startTime(startTime).endTime(endTime).status(taskStatus)
            .animalTask(true).subjectId(subject2.getId())
            .assignedEmployeeUsername(doctor.getUsername())
            .priority(true).build();

        String body = objectMapper.writeValueAsString(combinedTaskDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(doctor_login.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    public void updateTaskAsAdminInvalidTime_expectStatusConflict() throws Exception {
        String description = "New description";
        String title = "New title";
        LocalDateTime startTime = LocalDateTime.of(2020, 7, 12, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2020, 7, 12, 1, 0, 0);
        TaskStatus taskStatus = TaskStatus.NOT_ASSIGNED;

        Animal subject = animalRepository.save(animal);
        Animal subject2 = animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        userLoginRepository.save(doctor_login);
        employeeRepository.save(doctor);
        animalRepository.assignAnimalToCaretaker(anmial_caretaker.getUsername(),subject.getId());
        Task taskDef = taskRepository.save(task);

        AnimalTask animalTask = taskService.createAnimalTask(taskDef, subject);

        CombinedTaskDto combinedTaskDto = CombinedTaskDto.builder()
            .id(taskDef.getId()).title(title).description(description)
            .startTime(startTime).endTime(endTime).status(taskStatus)
            .animalTask(true).subjectId(subject2.getId())
            .assignedEmployeeUsername(doctor.getUsername())
            .priority(true).build();

        String body = objectMapper.writeValueAsString(combinedTaskDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(admin_login.getUsername(), ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    @Test
    public void invalidTime_notInWorkingTimeOfTheEmployee_expectStatusConflict() throws Exception {

        LocalDateTime start = LocalDateTime.of(2021, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2021, 1, 1, 1, 0);

        LocalTime workStart = LocalTime.of(8,0);
        LocalTime workEnd = LocalTime.of(18,0);

        TaskDto taskDto = TaskDto.builder()
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(start)
            .endTime(end)
            .build();

        Employee employee = Employee.builder()
            .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
            .name(NAME_ANIMAL_CARE_EMPLOYEE)
            .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
            .type(TYPE_ANIMAL_CARE_EMPLOYEE)
            .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
            .workTimeStart(workStart)
            .workTimeEnd(workEnd)
            .build();

        animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(employee);
        taskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        String body = objectMapper.writeValueAsString(taskDto);

        Animal savedAnimal = animalRepository.findAll().get(0);
        Employee savedEmployee = employeeRepository.findEmployeeByUsername(employee.getUsername());


        animalRepository.assignAnimalToCaretaker(savedEmployee.getUsername(),savedAnimal.getId());

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }


    // High Priority Tasks Tests
    @Test
    public void validEnclosureTaskWithHighPriority_createdByAdmin_returnsExpectedEnclosureTaskDtoWithHighPriority() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animal);
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);
        taskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        taskDto.setPriority(true);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ENCLOSURE_TASK_CREATION_BASE_URI + "/" + enclosure.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        AnimalTaskDto messageResponse = objectMapper.readValue(response.getContentAsString(),
            AnimalTaskDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertEquals(taskDto.getTitle(), messageResponse.getTitle()),
            () -> assertEquals(taskDto.getDescription(), messageResponse.getDescription()),
            () -> assertEquals(taskDto.getAssignedEmployeeUsername(), messageResponse.getAssignedEmployeeUsername()),
            () -> assertEquals(taskDto.getStartTime(), messageResponse.getStartTime()),
            () -> assertEquals(taskDto.getEndTime(), messageResponse.getEndTime()),
            () -> assertEquals(messageResponse.getStatus(), TaskStatus.ASSIGNED),
            () -> assertEquals(taskDto.isPriority(), messageResponse.isPriority())

        );
    }

    @Test
    public void validAnimalTaskWithHighPriority_createdByAdmin_returnsExpectedAnimalTaskDtoWithHighPriority() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animal);
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);
        taskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        taskDto.setPriority(true);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        AnimalTaskDto messageResponse = objectMapper.readValue(response.getContentAsString(),
            AnimalTaskDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertEquals(taskDto.getTitle(), messageResponse.getTitle()),
            () -> assertEquals(taskDto.getDescription(), messageResponse.getDescription()),
            () -> assertEquals(taskDto.getAssignedEmployeeUsername(), messageResponse.getAssignedEmployeeUsername()),
            () -> assertEquals(taskDto.getStartTime(), messageResponse.getStartTime()),
            () -> assertEquals(taskDto.getEndTime(), messageResponse.getEndTime()),
            () -> assertEquals(messageResponse.getStatus(), TaskStatus.ASSIGNED),
            () -> assertEquals(animal.getName(), messageResponse.getAnimalName()),
            () -> assertEquals(taskDto.isPriority(), messageResponse.isPriority())
        );
    }

    @Test
    public void validEnclosureTaskWithoutSettingPriority_createdByAdmin_returnsExpectedEnclosureTaskDtoWithHighPriorityFalse() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animal);
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);
        taskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ENCLOSURE_TASK_CREATION_BASE_URI + "/" + enclosure.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        AnimalTaskDto messageResponse = objectMapper.readValue(response.getContentAsString(),
            AnimalTaskDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertEquals(taskDto.getTitle(), messageResponse.getTitle()),
            () -> assertEquals(taskDto.getDescription(), messageResponse.getDescription()),
            () -> assertEquals(taskDto.getAssignedEmployeeUsername(), messageResponse.getAssignedEmployeeUsername()),
            () -> assertEquals(taskDto.getStartTime(), messageResponse.getStartTime()),
            () -> assertEquals(taskDto.getEndTime(), messageResponse.getEndTime()),
            () -> assertEquals(messageResponse.getStatus(), TaskStatus.ASSIGNED),
            () -> assertEquals(false, messageResponse.isPriority())

        );
    }

    @Test
    public void validAnimalTaskWithoutSettingPriority_createdByAdmin_returnsExpectedAnimalTaskDtoWithHighPriorityFalse() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animal);
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);
        taskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        String body = objectMapper.writeValueAsString(taskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(ANIMAL_TASK_CREATION_BASE_URI + "/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        AnimalTaskDto messageResponse = objectMapper.readValue(response.getContentAsString(),
            AnimalTaskDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertEquals(taskDto.getTitle(), messageResponse.getTitle()),
            () -> assertEquals(taskDto.getDescription(), messageResponse.getDescription()),
            () -> assertEquals(taskDto.getAssignedEmployeeUsername(), messageResponse.getAssignedEmployeeUsername()),
            () -> assertEquals(taskDto.getStartTime(), messageResponse.getStartTime()),
            () -> assertEquals(taskDto.getEndTime(), messageResponse.getEndTime()),
            () -> assertEquals(messageResponse.getStatus(), TaskStatus.ASSIGNED),
            () -> assertEquals(animal.getName(), messageResponse.getAnimalName()),
            () -> assertEquals(false, messageResponse.isPriority())
        );
    }

    @Test
    public void validRepeatableAnimalTask_createdByAdmin_returnsFirstAnimalTaskDto() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animal);
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);
        repeatableTaskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        String body = objectMapper.writeValueAsString(repeatableTaskDto);
        Animal savedAnimal = animalRepository.findAll().get(0);

        MvcResult mvcResult = this.mockMvc.perform(post(TASK_BASE_URI + "/animal/repeatable/" + savedAnimal.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        AnimalTaskDto messageResponse = objectMapper.readValue(response.getContentAsString(),
            AnimalTaskDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertEquals(repeatableTaskDto.getTitle(), messageResponse.getTitle()),
            () -> assertEquals(repeatableTaskDto.getDescription(), messageResponse.getDescription()),
            () -> assertEquals(repeatableTaskDto.getAssignedEmployeeUsername(), messageResponse.getAssignedEmployeeUsername()),
            () -> assertEquals(repeatableTaskDto.getStartTime(), messageResponse.getStartTime()),
            () -> assertEquals(repeatableTaskDto.getEndTime(), messageResponse.getEndTime()),
            () -> assertEquals(messageResponse.getStatus(), TaskStatus.ASSIGNED),
            () -> assertEquals(animal.getName(), messageResponse.getAnimalName())
        );
    }

    @Test
    public void validRepeatableEnclosureTask_createdByAdmin_returnsFirstEnclosureTaskDto() throws Exception {
        enclosureRepository.save(barn);
        Enclosure enclosure = enclosureRepository.findAll().get(0);
        animal.setEnclosure(enclosure);

        animalRepository.save(animal);
        List<Animal> animals = new LinkedList<>();
        animals.add(animal);
        userLoginRepository.save(animal_caretaker_login);
        anmial_caretaker.setAssignedAnimals(animals);
        employeeRepository.save(anmial_caretaker);
        repeatableTaskDto.setAssignedEmployeeUsername(USERNAME_ANIMAL_CARE_EMPLOYEE);
        String body = objectMapper.writeValueAsString(repeatableTaskDto);

        MvcResult mvcResult = this.mockMvc.perform(post(TASK_BASE_URI + "/enclosure/repeatable/" + enclosure.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        EnclosureTaskDto messageResponse = objectMapper.readValue(response.getContentAsString(),
            EnclosureTaskDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertEquals(repeatableTaskDto.getTitle(), messageResponse.getTitle()),
            () -> assertEquals(repeatableTaskDto.getDescription(), messageResponse.getDescription()),
            () -> assertEquals(repeatableTaskDto.getAssignedEmployeeUsername(), messageResponse.getAssignedEmployeeUsername()),
            () -> assertEquals(repeatableTaskDto.getStartTime(), messageResponse.getStartTime()),
            () -> assertEquals(repeatableTaskDto.getEndTime(), messageResponse.getEndTime()),
            () -> assertEquals(messageResponse.getStatus(), TaskStatus.ASSIGNED),
            () -> assertEquals(enclosure.getName(), messageResponse.getEnclosureName())
        );
    }

    @Test
    public void repeatDeleteTask_thenStatusOk() throws Exception {
        Animal savedAnimal = animalRepository.save(animal);
        Task savedTask = taskRepository.save(task);
        animalTaskRepository.save(AnimalTask.builder().id(savedTask.getId()).subject(savedAnimal).build());
        Task savedTask2 = taskRepository.save(Task.builder().title(task.getTitle())
            .description(task.getDescription())
            .startTime(task.getStartTime().plus(2, ChronoUnit.DAYS))
            .endTime(task.getEndTime().plus(2, ChronoUnit.DAYS))
            .status(task.getStatus())
            .build());
        animalTaskRepository.save(AnimalTask.builder().id(savedTask2.getId()).subject(savedAnimal).build());

        repeatableTaskRepository.save(RepeatableTask.builder().id(savedTask.getId()).followTask(savedTask2).build());
        repeatableTaskRepository.save(RepeatableTask.builder().id(savedTask2.getId()).followTask(null).build());

        MvcResult mvcResult = this.mockMvc.perform(delete(TASK_BASE_URI + "/repeatable/" + savedTask.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void repeatUpdateAnimalTask_thenStatusCreated() throws Exception {
        String description = "New description";
        String title = "New title";

        Animal subject = animalRepository.save(animal);
        Animal subject2 = animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        animalRepository.assignAnimalToCaretaker(anmial_caretaker.getUsername(),subject.getId());
        Task task1 = taskRepository.save(task);
        Task task2 = taskRepository.save(task);

        AnimalTask animalTask = animalTaskRepository.save(AnimalTask.builder().id(task1.getId()).subject(subject).build());
        AnimalTask animalTask2 = animalTaskRepository.save(AnimalTask.builder().id(task2.getId()).subject(subject).build());

        RepeatableTask repeatableTask = repeatableTaskRepository.save(RepeatableTask.builder().id(task1.getId()).followTask(task2).build());
        RepeatableTask repeatableTask2 = repeatableTaskRepository.save(RepeatableTask.builder().id(task2.getId()).followTask(null).build());

        CombinedTaskDto combinedTaskDto = CombinedTaskDto.builder()
            .id(task1.getId()).title(title).description(description)
            .animalTask(true).subjectId(subject2.getId())
            .build();

        String body = objectMapper.writeValueAsString(combinedTaskDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/update/repeat")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        AnimalTask updatedTask = taskService.getAnimalTaskById(task1.getId());
        assertAll(
            () -> {assertEquals(updatedTask.getTask().getTitle(),title);},
            () -> {assertEquals(updatedTask.getTask().getDescription(),description);},
            () -> {assertEquals(updatedTask.getSubject().getId(),subject2.getId());}
        );
    }

    @Test
    public void repeatUpdateAnimalTask_whenInvalid_thenStatusUnprocessableEntity() throws Exception {
        String description = "New description";
        String title = null;

        CombinedTaskDto combinedTaskDto = CombinedTaskDto.builder()
            .id(1L).title(title).description(description)
            .animalTask(true).subjectId(2L)
            .build();

        String body = objectMapper.writeValueAsString(combinedTaskDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/update/repeat")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void repeatUpdateEnclosureTask_thenStatusCreated() throws Exception {
        String description = "New description";
        String title = "new title";

        Enclosure subject = enclosureRepository.save(barn);
        Enclosure subject2 = enclosureRepository.save(Enclosure.builder().name("barn2").build());

        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Task task1 = taskRepository.save(task);
        Task task2 = taskRepository.save(task);

        EnclosureTask enclosureTask = enclosureTaskRepository.save(EnclosureTask.builder().id(task1.getId()).subject(subject).build());
        EnclosureTask enclosureTask1 = enclosureTaskRepository.save(EnclosureTask.builder().id(task2.getId()).subject(subject).build());

        RepeatableTask repeatableTask = repeatableTaskRepository.save(RepeatableTask.builder().id(task1.getId()).followTask(task2).build());
        RepeatableTask repeatableTask2 = repeatableTaskRepository.save(RepeatableTask.builder().id(task2.getId()).followTask(null).build());

        CombinedTaskDto combinedTaskDto = CombinedTaskDto.builder()
            .id(task1.getId()).title(title).description(description)
            .animalTask(false).subjectId(subject2.getId())
            .build();

        String body = objectMapper.writeValueAsString(combinedTaskDto);

        MvcResult mvcResult = this.mockMvc.perform(put(TASK_BASE_URI + "/update/repeat")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        EnclosureTask updatedTask = taskService.getEnclosureTaskById(task1.getId());
        assertAll(
            () -> {assertEquals(updatedTask.getTask().getTitle(),title);},
            () -> {assertEquals(updatedTask.getTask().getDescription(),description);},
            () -> {assertEquals(updatedTask.getSubject().getId(),subject2.getId());}
        );
    }

    @Test
    public void assignAnimalTaskDoctorRepeat() throws Exception {
        task.setStatus(TaskStatus.NOT_ASSIGNED);
        Animal subject = animalRepository.save(animal);
        userLoginRepository.save(doctor_login);
        employeeRepository.save(doctor);
        Task task1 = taskRepository.save(task);
        Task task2 = taskRepository.save(task);

        AnimalTask animalTask = animalTaskRepository.save(AnimalTask.builder().id(task1.getId()).subject(subject).build());
        AnimalTask animalTask2 = animalTaskRepository.save(AnimalTask.builder().id(task2.getId()).subject(subject).build());

        RepeatableTask repeatableTask = repeatableTaskRepository.save(RepeatableTask.builder().id(task1.getId()).followTask(task2).build());
        RepeatableTask repeatableTask2 = repeatableTaskRepository.save(RepeatableTask.builder().id(task2.getId()).followTask(null).build());

        MvcResult mvcResult = this.mockMvc.perform(post(TASK_BASE_URI + "/auto/animal/doctor/repeat/" + task1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        List<Task> tasks = taskRepository.findAll();
        for(Task t : tasks) {
            assertEquals(TaskStatus.ASSIGNED, t.getStatus());
            assertEquals(doctor.getUsername(), t.getAssignedEmployee().getUsername());
        }
    }

    @Test
    public void assignAnimalTaskCaretakerRepeat() throws Exception {
        task.setStatus(TaskStatus.NOT_ASSIGNED);
        Animal subject = animalRepository.save(animal);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        animalRepository.assignAnimalToCaretaker(anmial_caretaker.getUsername(),subject.getId());
        Task task1 = taskRepository.save(task);
        Task task2 = taskRepository.save(task);

        AnimalTask animalTask = animalTaskRepository.save(AnimalTask.builder().id(task1.getId()).subject(subject).build());
        AnimalTask animalTask2 = animalTaskRepository.save(AnimalTask.builder().id(task2.getId()).subject(subject).build());

        RepeatableTask repeatableTask = repeatableTaskRepository.save(RepeatableTask.builder().id(task1.getId()).followTask(task2).build());
        RepeatableTask repeatableTask2 = repeatableTaskRepository.save(RepeatableTask.builder().id(task2.getId()).followTask(null).build());

        MvcResult mvcResult = this.mockMvc.perform(post(TASK_BASE_URI + "/auto/animal/caretaker/repeat/" + task1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        List<Task> tasks = taskRepository.findAll();
        for(Task t : tasks) {
            assertEquals(TaskStatus.ASSIGNED, t.getStatus());
            assertEquals(anmial_caretaker.getUsername(), t.getAssignedEmployee().getUsername());
        }
    }

    @Test
    public void assignEnclosureTaskCaretakerRepeat() throws Exception {
        task.setStatus(TaskStatus.NOT_ASSIGNED);
        Enclosure subject = enclosureRepository.save(barn);
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        Animal animal1 = animalRepository.save(animal);
        animal1.setEnclosure(subject);
        animalRepository.save(animal1);
        animalRepository.assignAnimalToCaretaker(anmial_caretaker.getUsername(),animal1.getId());

        Task task1 = taskRepository.save(task);
        Task task2 = taskRepository.save(task);

        EnclosureTask enclosureTask = enclosureTaskRepository.save(EnclosureTask.builder().id(task1.getId()).subject(subject).build());
        EnclosureTask enclosureTask2 = enclosureTaskRepository.save(EnclosureTask.builder().id(task2.getId()).subject(subject).build());

        RepeatableTask repeatableTask = repeatableTaskRepository.save(RepeatableTask.builder().id(task1.getId()).followTask(task2).build());
        RepeatableTask repeatableTask2 = repeatableTaskRepository.save(RepeatableTask.builder().id(task2.getId()).followTask(null).build());

        MvcResult mvcResult = this.mockMvc.perform(post(TASK_BASE_URI + "/auto/enclosure/caretaker/repeat/" + task1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        List<Task> tasks = taskRepository.findAll();
        for(Task t : tasks) {
            assertEquals(TaskStatus.ASSIGNED, t.getStatus());
            assertEquals(anmial_caretaker.getUsername(), t.getAssignedEmployee().getUsername());
        }
    }

    @Test
    public void assignEnclosureTaskJanitorRepeat() throws Exception {
        task.setStatus(TaskStatus.NOT_ASSIGNED);
        Enclosure subject = enclosureRepository.save(barn);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(janitor);

        Task task1 = taskRepository.save(task);
        Task task2 = taskRepository.save(task);

        EnclosureTask enclosureTask = enclosureTaskRepository.save(EnclosureTask.builder().id(task1.getId()).subject(subject).build());
        EnclosureTask enclosureTask2 = enclosureTaskRepository.save(EnclosureTask.builder().id(task2.getId()).subject(subject).build());

        RepeatableTask repeatableTask = repeatableTaskRepository.save(RepeatableTask.builder().id(task1.getId()).followTask(task2).build());
        RepeatableTask repeatableTask2 = repeatableTaskRepository.save(RepeatableTask.builder().id(task2.getId()).followTask(null).build());

        MvcResult mvcResult = this.mockMvc.perform(post(TASK_BASE_URI + "/auto/enclosure/janitor/repeat/" + task1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        List<Task> tasks = taskRepository.findAll();
        for(Task t : tasks) {
            assertEquals(TaskStatus.ASSIGNED, t.getStatus());
            assertEquals(janitor.getUsername(), t.getAssignedEmployee().getUsername());
        }
    }
}