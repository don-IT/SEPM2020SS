package at.ac.tuwien.sepm.groupphase.backend.integrationtest.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EmployeeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewPasswordReqDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EmployeeMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.NewPasswordReq;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserLoginRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EmployeeEndpointTest implements TestData {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    //for testing assignment
    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private AnimalMapper animalMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;


    String GET_FILTERED_EMPLOYEES_URI = EMPLOYEE_BASE_URI + "/search";
    String EMPLOYEE_INFO_PAGE = EMPLOYEE_BASE_URI + "/info";

    private UserLogin admin_login = UserLogin.builder()
        .isAdmin(true)
        .username(ADMIN_USER)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private UserLogin default_user_login = UserLogin.builder()
        .isAdmin(false)
        .username(DEFAULT_USER)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
        .build();

    private Employee default_user = Employee.builder()
        .username(DEFAULT_USER)
        .name(NAME_JANITOR_EMPLOYEE)
        .birthday(BIRTHDAY_JANITOR_EMPLOYEE)
        .type(TYPE_JANITOR_EMPLOYEE)
        .email(EMAIL_JANITOR_EMPLOYEE)
        .workTimeStart(TEST_LOCAL_TIME_START)
        .workTimeEnd(TEST_LOCAL_TIME_END)
        .build();

    private UserLogin animal_caretaker_login = UserLogin.builder()
        .isAdmin(false)
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
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

    private UserLogin janitor_login = UserLogin.builder()
        .isAdmin(false)
        .username(USERNAME_JANITOR_EMPLOYEE)
        .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
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


    @BeforeEach
    public void beforeEach(){
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
        animalRepository.deleteAll();
        animal_caretaker_login = UserLogin.builder()
            .isAdmin(false)
            .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
            .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
            .build();

        anmial_caretaker = Employee.builder()
            .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
            .name(NAME_ANIMAL_CARE_EMPLOYEE)
            .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
            .type(TYPE_ANIMAL_CARE_EMPLOYEE)
            .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
            .workTimeStart(TEST_LOCAL_TIME_START)
            .workTimeEnd(TEST_LOCAL_TIME_END)
            .build();

        doctor_login = UserLogin.builder()
            .isAdmin(false)
            .username(USERNAME_DOCTOR_EMPLOYEE)
            .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
            .build();

        doctor = Employee.builder()
            .username(USERNAME_DOCTOR_EMPLOYEE)
            .name(NAME_DOCTOR_EMPLOYEE)
            .birthday(BIRTHDAY_DOCTOR_EMPLOYEE)
            .type(TYPE_DOCTOR_EMPLOYEE)
            .email(EMAIL_DOCTOR_EMPLOYEE)
            .workTimeStart(TEST_LOCAL_TIME_START)
            .workTimeEnd(TEST_LOCAL_TIME_END)
            .build();

        janitor_login = UserLogin.builder()
            .isAdmin(false)
            .username(USERNAME_JANITOR_EMPLOYEE)
            .password(passwordEncoder.encode(VALID_TEST_PASSWORD))
            .build();

        janitor = Employee.builder()
            .username(USERNAME_JANITOR_EMPLOYEE)
            .name(NAME_JANITOR_EMPLOYEE)
            .birthday(BIRTHDAY_JANITOR_EMPLOYEE)
            .type(TYPE_JANITOR_EMPLOYEE)
            .email(EMAIL_JANITOR_EMPLOYEE)
            .workTimeStart(TEST_LOCAL_TIME_START)
            .workTimeEnd(TEST_LOCAL_TIME_END)
            .build();

        horse = Animal.builder()
            .id(ANIMAL_ID)
            .name(ANIMAL_NAME_HORSE)
            .description(ANIMAL_DESCRIPTION_FAST)
            .species(ANIMAL_SPECIES_ARABIAN)
            .publicInformation(ANIMAL_PUBLIC_INFORMATION_FAMOUS)
            .build();

        userAnimalCareEmployee= UserLogin.builder()
            .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
            .password("something6")
            .build();
    }

    @AfterEach
    public void afterEach(){
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
        animalRepository.deleteAll();
    }

    @Test
    public void emptyRepository_whenFindAll_thenEmptyList() throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(get(EMPLOYEE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EmployeeDto> employeeDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EmployeeDto[].class));

        assertEquals(0, employeeDtos.size());
    }

    @Test
    public void repositoryWithAllTypes_whenFindDoctor_thenOnlyDoctorInList() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(GET_FILTERED_EMPLOYEES_URI + "?type=DOCTOR")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EmployeeDto> employeeDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EmployeeDto[].class));
        assertEquals(1, employeeDtos.size());
        assertEquals(employeeDtos.get(0).getType(), EmployeeType.DOCTOR);
        assertEquals(employeeDtos.get(0).getUsername(), doctor.getUsername());
    }

    @Test
    public void repositoryWithAllTypes_whenFindAnimalCaretaker_thenOnlyAnimalCaretakerInList() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(GET_FILTERED_EMPLOYEES_URI + "?type=ANIMAL_CARE")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EmployeeDto> employeeDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EmployeeDto[].class));
        assertEquals(1, employeeDtos.size());
        assertEquals(employeeDtos.get(0).getType(), EmployeeType.ANIMAL_CARE);
        assertEquals(employeeDtos.get(0).getUsername(), anmial_caretaker.getUsername());
    }

    @Test
    public void repositoryWithAllTypes_whenFindJanitor_thenOnlyJanitorInList() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(GET_FILTERED_EMPLOYEES_URI + "?type=JANITOR")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EmployeeDto> employeeDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EmployeeDto[].class));
        assertEquals(1, employeeDtos.size());
        assertEquals(employeeDtos.get(0).getType(), EmployeeType.JANITOR);
        assertEquals(employeeDtos.get(0).getUsername(), janitor.getUsername());
    }

    @Test
    public void repositoryWithAllTypes_whenFindAll_thenReturnAll() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(EMPLOYEE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EmployeeDto> employeeDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EmployeeDto[].class));
        assertEquals(3, employeeDtos.size());
        //all three should be different
        assertNotSame(employeeDtos.get(0), employeeDtos.get(1));
        assertNotSame(employeeDtos.get(1), employeeDtos.get(2));
        assertNotSame(employeeDtos.get(0), employeeDtos.get(2));
    }

    @Test
    public void filledRepository_whenSearchName_thenReturnAllWithSubstringNameCaseInsensitive() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(GET_FILTERED_EMPLOYEES_URI + "?name=aN")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EmployeeDto> employeeDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EmployeeDto[].class));

        assertEquals(2, employeeDtos.size());
        assertNotSame(employeeDtos.get(0), employeeDtos.get(1));
    }

    @Test
    public void filledRepository_whenCombinedSearchNameType_thenReturnAllWithSubstringNameCaseInsensitiveTypeJanitor() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(GET_FILTERED_EMPLOYEES_URI + "?name=aN&type=JANITOR")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EmployeeDto> employeeDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EmployeeDto[].class));

        assertEquals(1, employeeDtos.size());
        assertNotSame(employeeDtos.get(0).getUsername(), janitor.getUsername());
    }

    @Test
    public void filledRepository_whenSearchWithNoMatch_thenReturnNotFoundException() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(GET_FILTERED_EMPLOYEES_URI + "?name=notInRepository")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        assertEquals(response.getContentAsString(), "No employee fits the given criteria");
    }

    @Test
    public void returnUserData_whenSearchEmployeeInfoAsAdmin_StatusOk() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        MvcResult mvcResult = this.mockMvc.perform(get(EMPLOYEE_BASE_URI+"/"+anmial_caretaker.getUsername())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(),response.getStatus());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON_VALUE);
        EmployeeDto employeeDto = objectMapper.readValue(response.getContentAsString(),EmployeeDto.class);
        assertEquals(employeeDto.getName(),anmial_caretaker.getName());
    }

    @Test
    public void whenEmployeeAccessOtherUserInformation_statusForbidden() throws Exception{
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        MvcResult mvcResult = this.mockMvc.perform(get(EMPLOYEE_BASE_URI+"/"+anmial_caretaker.getUsername())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(),response.getStatus());
    }

    @Test
    public void whenAdminAccessInfoPage_statusNotFound() throws Exception{
        userLoginRepository.save(admin_login);
        MvcResult mvcResult = this.mockMvc.perform(get(EMPLOYEE_INFO_PAGE)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(),response.getStatus());
        assertEquals(response.getContentAsString(),"Administrators do not have an info page.");
    }

    @Test
    public void whenEmployeeAccessInfoPage_statusOk() throws Exception{
        userLoginRepository.save(default_user_login);
        employeeRepository.save(default_user);
        MvcResult mvcResult = this.mockMvc.perform(get(EMPLOYEE_INFO_PAGE)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(),response.getStatus());
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON_VALUE);
        EmployeeDto employeeDto = objectMapper.readValue(response.getContentAsString(),EmployeeDto.class);
        assertEquals(employeeDto.getName(),default_user.getName());
    }

    @Test
    public void assigningAnimalToEmployee() throws Exception {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        animalRepository.save(horse);
        horse = animalRepository.findAll().get(0);
        AnimalDto animalDto = animalMapper.animalToAnimalDto(horse);
        String body = objectMapper.writeValueAsString(animalDto);

        MvcResult mvcResult = this.mockMvc.perform(post(EMPLOYEE_BASE_URI + "/animal/" + anmial_caretaker.getUsername())
            .content(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());

    }

    @Test
    public void whenAdminCreateEmployeeWithValidData_statusCreated() throws Exception{

        EmployeeDto emp = EmployeeDto.builder()
            .name(NAME_ANIMAL_CARE_EMPLOYEE)
            .type(TYPE_ANIMAL_CARE_EMPLOYEE)
            .birthday(BIRTHDAY_JANITOR_EMPLOYEE)
            .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
            .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
            .workTimeStart(TEST_LOCAL_TIME_START)
            .workTimeEnd(TEST_LOCAL_TIME_END)
            .password("Password1")
            .build();

        String body = objectMapper.writeValueAsString(emp);

        MvcResult mvcResult = this.mockMvc.perform(post(EMPLOYEE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(),  response.getStatus());
    }

    @Test
    public void whenAdminCreateEmployeeWithInvalidData_statusBadRequest() throws Exception{
        userLoginRepository.save(admin_login);

        EmployeeDto emp = EmployeeDto.builder()
            .name("")
            .type(EmployeeType.ANIMAL_CARE)
            .birthday(BIRTHDAY_JANITOR_EMPLOYEE)
            .email("test@email.com")
            .username("")
            .password("")
            .build();

        String body = objectMapper.writeValueAsString(emp);

        MvcResult mvcResult = this.mockMvc.perform(post(EMPLOYEE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(),  response.getStatus());
    }

    @Test
    public void whenEmployeeCreatEmploeeWithValidData_StatusForbidden() throws Exception{
        userLoginRepository.save(default_user_login);
        employeeRepository.save(default_user);

        EmployeeDto emp = EmployeeDto.builder()
            .name("Test")
            .type(EmployeeType.ANIMAL_CARE)
            .birthday(BIRTHDAY_JANITOR_EMPLOYEE)
            .email("test@email.com")
            .username("tester")
            .password("Password1")
            .workTimeStart(TEST_LOCAL_TIME_START)
            .workTimeEnd(TEST_LOCAL_TIME_END)
            .build();

        String body = objectMapper.writeValueAsString(emp);

        MvcResult mvcResult = this.mockMvc.perform(post(EMPLOYEE_BASE_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(),  response.getStatus());
    }

    @Test
    public void deletingEmployee() throws Exception {
        userLoginRepository.save(animal_caretaker_login);
        employeeRepository.save(anmial_caretaker);
        List<Employee> employees= employeeRepository.findAll();
        Employee employee = employees.get(0);
        MvcResult mvcResult = this.mockMvc.perform(delete(EMPLOYEE_BASE_URI +"/"+ employee.getUsername())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(),  response.getStatus());

    }

    @Test
    public void editingEmployeeEmptyName() throws Exception {
        userLoginRepository.save(default_user_login);
        employeeRepository.save(default_user);
        default_user.setName("");
        String body = objectMapper.writeValueAsString(employeeMapper.employeeToEmployeeDto(default_user));

        MvcResult mvcResult = this.mockMvc.perform(put(EMPLOYEE_BASE_URI+ "/edit/"+ default_user.getUsername())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(),  response.getStatus());
    }

    @Test
    public void editingEmployeeName() throws Exception {
        EmployeeDto emp = EmployeeDto.builder()
            .name("Test")
            .type(EmployeeType.ANIMAL_CARE)
            .birthday(BIRTHDAY_JANITOR_EMPLOYEE)
            .email("test@email.com")
            .username("tester")
            .password("Password1")
            .workTimeStart(TEST_LOCAL_TIME_START)
            .workTimeEnd(TEST_LOCAL_TIME_END)
            .build();
        employeeRepository.save(employeeMapper.employeeDtoToEmployee(emp));

        emp.setName("Don");
        String body = objectMapper.writeValueAsString(emp);

        MvcResult mvcResult = this.mockMvc.perform(put(EMPLOYEE_BASE_URI+ "/edit/"+ emp.getUsername())
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        List<Employee> employees = this.employeeRepository.findAll();
        assertEquals("Don", employees.get(0).getName());
        assertEquals(HttpStatus.OK.value(),  response.getStatus());
    }

    @Test
    public void changePasswordByAdmin() throws Exception{
        userLoginRepository.save(default_user_login);
        NewPasswordReqDto newRequest= NewPasswordReqDto.builder()
            .newPassword("TestPassword1")
            .username(default_user_login.getUsername())
            .build();
        String body = objectMapper.writeValueAsString(newRequest);
        MvcResult mvcResult = this.mockMvc.perform(put(EMPLOYEE_BASE_URI+ "/editPasswordByAdmin/")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        boolean newPassword = passwordEncoder.matches(newRequest.getNewPassword(), userLoginRepository.findUserByUsername(default_user_login.getUsername()).getPassword());
        assertTrue(newPassword);
        assertEquals(HttpStatus.OK.value(),  response.getStatus());
    }

    @Test
    public void changePasswordByAdminNotAdmin() throws Exception{
        userLoginRepository.save(default_user_login);
        NewPasswordReqDto newRequest= NewPasswordReqDto.builder()
            .newPassword("TestPassword1")
            .username(default_user_login.getUsername())
            .build();
        String body = objectMapper.writeValueAsString(newRequest);
        MvcResult mvcResult = this.mockMvc.perform(put(EMPLOYEE_BASE_URI+ "/editPasswordByAdmin/")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(),  response.getStatus());
    }

    @Test
    public void changePasswordAsEmployee() throws Exception{
        userLoginRepository.save(default_user_login);
        NewPasswordReqDto newRequest= NewPasswordReqDto.builder()
            .currentPassword("Password1")
            .newPassword("TestPassword1")
            .username(default_user_login.getUsername())
            .build();
        String body = objectMapper.writeValueAsString(newRequest);
        MvcResult mvcResult = this.mockMvc.perform(put(EMPLOYEE_BASE_URI+ "/editPassword/")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        boolean newPassword = passwordEncoder.matches(newRequest.getNewPassword(), userLoginRepository.findUserByUsername(default_user_login.getUsername()).getPassword());
        assertTrue(newPassword);
        assertEquals(HttpStatus.OK.value(),  response.getStatus());
    }

    @Test
    public void changePasswordAsEmployeeWrongCurrentPassword() throws Exception{
        userLoginRepository.save(default_user_login);
        NewPasswordReqDto newRequest= NewPasswordReqDto.builder()
            .currentPassword("Password2")
            .newPassword("TestPassword1")
            .username(default_user_login.getUsername())
            .build();
        String body = objectMapper.writeValueAsString(newRequest);
        MvcResult mvcResult = this.mockMvc.perform(put(EMPLOYEE_BASE_URI+ "/editPassword/")
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER, USER_ROLES))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        boolean newPassword = passwordEncoder.matches(newRequest.getNewPassword(), userLoginRepository.findUserByUsername(default_user_login.getUsername()).getPassword());
        assertFalse(newPassword);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(),  response.getStatus());
    }
}
