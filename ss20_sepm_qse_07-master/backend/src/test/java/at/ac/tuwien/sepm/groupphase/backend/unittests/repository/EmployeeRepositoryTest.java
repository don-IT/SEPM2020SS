package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserLoginRepository;
import org.junit.jupiter.api.AfterEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class EmployeeRepositoryTest implements TestData {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserLoginRepository userLoginRepository;

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

    @BeforeEach
    public void beforeEach(){
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
    }

    @AfterEach
    public void afterEach(){
        employeeRepository.deleteAll();
        userLoginRepository.deleteAll();
    }

    @Test
    public void emptyRepository_whenFindAll_thenEmptyList() {
        List<Employee> employees = employeeRepository.findAll();
        assertEquals(0, employees.size());
    }

    @Test
    public void filledRepository_whenFindAll_thenListOfAllEmployees() {
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        List<Employee> employees = employeeRepository.findAll();
        assertEquals(3, employees.size());
        assertTrue(employees.contains(janitor));
        assertTrue(employees.contains(anmial_caretaker));
        assertTrue(employees.contains(doctor));
    }

    @Test
    public void findByUsername_returnsRightUser(){
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        Employee employee = employeeRepository.findEmployeeByUsername(doctor_login.getUsername());
        assertEquals(employee.getUsername(),doctor_login.getUsername());
    }

    @Test
    public void findByUsername_returnsNullIfNoSuchEmployeeExists(){
        userLoginRepository.save(animal_caretaker_login);
        userLoginRepository.save(doctor_login);
        userLoginRepository.save(janitor_login);
        employeeRepository.save(anmial_caretaker);
        employeeRepository.save(doctor);
        employeeRepository.save(janitor);
        Employee employee = employeeRepository.findEmployeeByUsername("iDoNotExist");
        assertEquals(employee,null);
    }
}
