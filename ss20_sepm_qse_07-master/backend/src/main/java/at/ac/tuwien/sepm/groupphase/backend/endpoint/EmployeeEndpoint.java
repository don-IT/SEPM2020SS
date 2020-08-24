package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EmployeeMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewPasswordReqMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserLoginMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.EnclosureService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EmployeeService employeeService;
    private final UserService userService;
    private final EmployeeMapper employeeMapper;
    private final UserLoginMapper userLoginMapper;
    private final AnimalMapper animalMapper;
    private final AnimalService animalService;
    private final EnclosureService enclosureService;
    private final NewPasswordReqMapper newPasswordReqMapper;

    @Autowired
    public EmployeeEndpoint(EmployeeService employeeService, UserService userService,
                            EmployeeMapper employeeMapper, UserLoginMapper userLoginMapper,
                            AnimalMapper animalMapper, AnimalService animalService, EnclosureService enclosureService, NewPasswordReqMapper newPasswordReqMapper){
        this.animalService = animalService;
        this.employeeService=employeeService;
        this.userService=userService;
        this.employeeMapper=employeeMapper;
        this.userLoginMapper=userLoginMapper;
        this.animalMapper = animalMapper;
        this.enclosureService = enclosureService;
        this.newPasswordReqMapper = newPasswordReqMapper;
    }


    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "")
    @ApiOperation(value = "Create new employee", authorizations = {@Authorization(value = "apiKey")})
    public EmployeeDto createEmployee(@RequestBody @Valid EmployeeDto employeeDto){
        LOGGER.info("POST /api/v1/employee body: {}",employeeDto);
        userService.createNewUser(userLoginMapper.eployeeDtoToUserLogin(employeeDto));
        return employeeMapper.employeeToEmployeeDto(employeeService.createEmployee(employeeMapper.employeeDtoToEmployee(employeeDto)));
    }


    /**
     * Get Method for all current employees only Admin has permissions to see the list of employees
     * @return a List of All current employees
     */
    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @ApiOperation(value = "Get list of employees without details", authorizations = {@Authorization(value = "apiKey")})
    public List<EmployeeDto> getAllEmployees(){
        LOGGER.info("GET /api/v1/employee");
        List<Employee> employees = employeeService.getAll();
        List<EmployeeDto> employeeDtos = new LinkedList<>();
        for(Employee e: employees){
            employeeDtos.add(employeeMapper.employeeToEmployeeDto(e));
        }
        return employeeDtos;
    }

    /**
     * Get Method for filtered list of all current employees only Admin has permissions to see the list of employees
     * search fields can be combined. If a field is null it is not taken into consideration
     * @param name search for substring of employee name
     * @param type search for employee type
     * @return a List of All current employees
     */
    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/search")
    @ApiOperation(value = "Get list of employees matching name and type", authorizations = {@Authorization(value = "apiKey")})
    public List<EmployeeDto> searchEmployees(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "type", required = false) EmployeeType type){
        LOGGER.info("GET /api/v1/employee/search Name: {} Type: {}", name, type);
        Employee searchEmployee = Employee.builder().name(name).type(type).build();
        List<Employee> employees = employeeService.findByNameAndType(searchEmployee);
        List<EmployeeDto> employeeDtos = new LinkedList<>();
        for(Employee e: employees){
            employeeDtos.add(employeeMapper.employeeToEmployeeDto(e));
        }
        return employeeDtos;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/animal/{employeeUsername}")
    @ApiOperation(value = "Get list of animals assigned to employee", authorizations = {@Authorization(value = "apiKey")})
    public List<AnimalDto> searchAnimals(@PathVariable String employeeUsername) {
        LOGGER.info("GET /api/v1/employee/animal/{}", employeeUsername);
        List<Animal> animals = employeeService.findAssignedAnimals(employeeUsername);
        List<AnimalDto> animalDtos = new LinkedList<>();
        for(Animal a: animals) {
           animalDtos.add(animalMapper.animalToAnimalDto(a));
       }
        return animalDtos;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/animal/{employeeUsername}")
    @ApiOperation(value = "Assign animal to Employee", authorizations = {@Authorization(value = "apiKey")})
    public void assignAnimal(@PathVariable String employeeUsername, @RequestBody AnimalDto animal) {
        LOGGER.info("Post /api/v1/employee/animal/{} Animal: {}", employeeUsername, animal.getId());
        employeeService.assignAnimal(employeeUsername, animal.getId());
    }


    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/animal/{employeeUsername}")
    @ApiOperation(value = "Remove assigned animal from employee", authorizations = {@Authorization(value = "apiKey")})
    public void removeAssignedAnimal(@PathVariable String employeeUsername, @RequestBody AnimalDto animal) {
        LOGGER.info("Post /api/v1/employee/animal/{} Animal: {}", employeeUsername, animal.getId());
        employeeService.removeAssignedAnimal(employeeUsername, animal.getId());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/{username}")
    @ApiOperation(value = "Get detailed information about a specific employee",
        authorizations = {@Authorization(value = "apiKey")})
    public EmployeeDto find(@PathVariable String username,Authentication authentication) {
        LOGGER.info("GET /api/v1/employee/{}", username);
        return employeeMapper.employeeToEmployeeDto(employeeService.findByUsername(username));
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/info")
    @ApiOperation(value = "Get detailed information about a myself",
        authorizations = {@Authorization(value = "apiKey")})
    public EmployeeDto personalInfo(Authentication authentication){
        LOGGER.info("GET /api/v1/employee/info");
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
      if(isAdmin){
          throw new NotFoundException("Administrators do not have an info page.");
      }else{
          String username = (String)authentication.getPrincipal();
          return employeeMapper.employeeToEmployeeDto(employeeService.findByUsername(username));
      }
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/assigned/animal/{animalId}")
    @ApiOperation(value = "Get list of animals assigned to employee", authorizations = {@Authorization(value = "apiKey")})
    public List<EmployeeDto> getAllAssignedToAnimal(@PathVariable Long animalId) {
        LOGGER.info("GET /api/v1/employee/assigned/{}", animalId);
        Animal animal = animalService.findAnimalById(animalId);
        List<Employee> employees = employeeService.getAllAssignedToAnimal(animal);
        List<EmployeeDto> employeeDtos = new LinkedList<>();
        for(Employee e: employees) {
            employeeDtos.add(employeeMapper.employeeToEmployeeDto(e));
        }
        return employeeDtos;
    }


    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/assigned/enclosure/{enclosureId}")
    @ApiOperation(value = "Get list of employees assigned to enclosure", authorizations = {@Authorization(value = "apiKey")})
    public List<EmployeeDto> getAllAssignedToEnclosure(@PathVariable Long enclosureId) {
        LOGGER.info("GET /api/v1/employee/assigned/enclosure/{}", enclosureId);
        Enclosure enclosure = enclosureService.findById_WithoutTasksAndAnimals(enclosureId);
        List<Employee> employees = employeeService.getAllAssignedToEnclosure(enclosure);
        List<EmployeeDto> employeeDtos = new LinkedList<>();
        for(Employee e: employees) {
            employeeDtos.add(employeeMapper.employeeToEmployeeDto(e));
        }
        return employeeDtos;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/doctors")
    @ApiOperation(value = "Get list of all Doctors", authorizations = {@Authorization(value = "apiKey")})
    public List<EmployeeDto> getAllDocotrs() {
        LOGGER.info("GET /api/v1/employee/doctors");
        List<Employee> employees = employeeService.getAllDocotrs();
        List<EmployeeDto> employeeDtos = new LinkedList<>();
        for(Employee e: employees) {
            employeeDtos.add(employeeMapper.employeeToEmployeeDto(e));
        }
        return employeeDtos;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/janitors")
    @ApiOperation(value = "Get list of all Janitors", authorizations = {@Authorization(value = "apiKey")})
    public List<EmployeeDto> getAllJanitors() {
        LOGGER.info("GET /api/v1/employee/janitors");
        List<Employee> employees = employeeService.getAllJanitors();
        List<EmployeeDto> employeeDtos = new LinkedList<>();
        for(Employee e: employees) {
            employeeDtos.add(employeeMapper.employeeToEmployeeDto(e));
        }
        return employeeDtos;
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(value = "/{username}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete a specific employee",
        authorizations = {@Authorization(value = "apiKey")})
    public void deleteEmployee(@PathVariable String username) {
        LOGGER.info("DELETE /api/v1/employee/{}", username);
       employeeService.deleteEmployeeByUsername(username);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/user/{username}")
    @ApiOperation(value = "Get detailed information about a specific user",
        authorizations = {@Authorization(value = "apiKey")})
    public UserLoginDto findUser(@PathVariable String username, Authentication authentication) {
        LOGGER.info("GET /api/v1/user/{}", username);
        return userLoginMapper.userLoginToUserLoginDto(userService.findApplicationUserByUsername(username));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/edit/{oldUsername}")
    @ApiOperation(value = "Edit employee", authorizations = {@Authorization(value = "apiKey")})
    public EmployeeDto editEmployee(@RequestBody @Valid EmployeeDto employeeDto, @PathVariable String oldUsername){
        LOGGER.info("PUT /api/v1/employee /edit/ body: {}",employeeDto);
        return employeeMapper.employeeToEmployeeDto(employeeService.editEmployee(employeeMapper.employeeDtoToEmployee(employeeDto), oldUsername));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/editPassword/")
    @ApiOperation(value = "Change Password", authorizations = {@Authorization(value = "apiKey")})
    public void editPassword(@RequestBody @Valid NewPasswordReqDto newPasswordReqDto){
        LOGGER.info("PUT /api/v1/employee /editPassword/ body: {}",newPasswordReqDto);
        userService.changePassword(newPasswordReqMapper.newPasswordReqDtoToNewPasswordReq(newPasswordReqDto));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/editPasswordByAdmin/")
    @ApiOperation(value = "Change Password", authorizations = {@Authorization(value = "apiKey")})
    public void editPasswordByAdmin(@RequestBody @Valid NewPasswordReqDto newPasswordReqDto){
        LOGGER.info("PUT /api/v1/employee /editPasswordByAdmin/ body: {}",newPasswordReqDto);
        userService.changePasswordByAdmin(newPasswordReqMapper.newPasswordReqDtoToNewPasswordReq(newPasswordReqDto));
    }

}
