package at.ac.tuwien.sepm.groupphase.backend.unittests.mapper;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EmployeeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EmployeeMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmployerMappingTest implements TestData {

    private final Employee employee = Employee.builder()
        .username(USERNAME_JANITOR_EMPLOYEE)
        .name(NAME_JANITOR_EMPLOYEE)
        .birthday(BIRTHDAY_JANITOR_EMPLOYEE)
        .type(TYPE_JANITOR_EMPLOYEE)
        .email(EMAIL_JANITOR_EMPLOYEE)
        .build();

    private final EmployeeDto employeeDto = EmployeeDto.builder()
        .username(USERNAME_JANITOR_EMPLOYEE)
        .name(NAME_JANITOR_EMPLOYEE)
        .birthday(BIRTHDAY_JANITOR_EMPLOYEE)
        .type(TYPE_JANITOR_EMPLOYEE)
        .email(EMAIL_JANITOR_EMPLOYEE)
        .build();

    @Autowired
    EmployeeMapper employeeMapper;

    @Test
    public void givenNothing_whenMapEmployeeToEmployeeDto_thenDtoHasAllProperties() {
        EmployeeDto tempEmployeeDto = employeeMapper.employeeToEmployeeDto(employee);

        assertAll(
            () -> assertEquals(employee.getUsername(), tempEmployeeDto.getUsername()),
            () -> assertEquals(employee.getName(), tempEmployeeDto.getName()),
            () -> assertEquals(employee.getBirthday(), tempEmployeeDto.getBirthday()),
            () -> assertEquals(employee.getEmail(), tempEmployeeDto.getEmail()),
            () -> assertEquals(employee.getType(), tempEmployeeDto.getType())
        );
    }

    @Test
    public void givenNothing_whenMapEmployeeDtoToEmployee_thenEmployeeHasAllProperties() {
        Employee tempEmployee = employeeMapper.employeeDtoToEmployee(employeeDto);

        assertAll(
            () -> assertEquals(tempEmployee.getUsername(), employeeDto.getUsername()),
            () -> assertEquals(tempEmployee.getName(), employeeDto.getName()),
            () -> assertEquals(tempEmployee.getBirthday(), employeeDto.getBirthday()),
            () -> assertEquals(tempEmployee.getEmail(), employeeDto.getEmail()),
            () -> assertEquals(tempEmployee.getType(), employeeDto.getType())
        );
    }
}
