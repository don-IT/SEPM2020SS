package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TaskCommentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TaskCommentMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.entity.TaskComment;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotAuthorisedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserLoginRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskCommentService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
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
@RequestMapping(value = "/api/v1/comments")
public class TaskCommentEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TaskCommentMapper taskCommentMapper;
    private final TaskCommentService taskCommentService;
    private final EmployeeService employeeService;
    private final TaskService taskService;
    private final UserLoginRepository userLoginRepository;

    @Autowired
    public TaskCommentEndpoint(TaskCommentMapper taskCommentMapper, TaskCommentService taskCommentService,
                               EmployeeService employeeService, TaskService taskService, UserLoginRepository userLoginRepository) {
        this.taskCommentMapper = taskCommentMapper;
        this.taskCommentService = taskCommentService;
        this.employeeService = employeeService;
        this.taskService = taskService;
        this.userLoginRepository = userLoginRepository;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{taskId}")
    @ApiOperation(value = "Get list of comments belonging to a task", authorizations = {@Authorization(value = "apiKey")})
    public List<TaskCommentDto> getCommentsOfTask(@PathVariable Long taskId, Authentication authentication) {
        LOGGER.info("GET /api/v1/comments/ {}", taskId);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        String username = (String) authentication.getPrincipal();
        if(!isAdmin && !isAuthorized(username, taskId)) {
            throw new NotAuthorisedException("You are not allowed to see this tasks comments.");
        }
        List<TaskComment> taskComments = taskCommentService.findAllByTaskId(taskId);
        List<TaskCommentDto> taskCommentDtos = new LinkedList<>();
        for (TaskComment t : taskComments) {
            taskCommentDtos.add(taskCommentMapper.taskCommentToTaskCommentDto(t));
        }
        return taskCommentDtos;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    @ApiOperation(value = "Create a comment of a task", authorizations = {@Authorization(value = "apiKey")})
    public TaskCommentDto createCommentOfTask(@Valid @RequestBody TaskCommentDto taskCommentDto, Authentication authentication) {
        LOGGER.info("POST /api/v1/comments");
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        String username = (String) authentication.getPrincipal();
        if(!isAdmin && !isAuthorized(username, taskCommentDto.getTaskId())) {
            throw new NotAuthorisedException("You are not allowed to create a comment for the given task.");
        }
        TaskComment taskComment = taskCommentMapper.taskCommentDtoToTaskComment(taskCommentDto);
        UserLogin creator = userLoginRepository.findUserByUsername(username);
        Task task = taskService.getTaskById(taskCommentDto.getTaskId());
        taskComment.setCreator(creator);
        taskComment.setTask(task);
        return taskCommentMapper.taskCommentToTaskCommentDto(taskCommentService.createComment(taskComment));
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{taskCommentId}")
    @ApiOperation(value = "Delete a comment of a task", authorizations = {@Authorization(value = "apiKey")})
    public void deleteCommentOfTask(@PathVariable Long taskCommentId, Authentication authentication) {
        LOGGER.info("DELETE /api/v1/comments/ {}", taskCommentId);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        String username = (String) authentication.getPrincipal();

        TaskComment taskComment = taskCommentService.findById(taskCommentId);

        if(!isAdmin && !isAuthorized(username, taskComment.getTask().getId())) {
            throw new NotAuthorisedException("You are not allowed to delete this comment.");
        }
        taskCommentService.delete(taskCommentId);
    }

    private boolean isAuthorized(String username, Long taskId) {
        Employee employee = employeeService.findByUsername(username);
        if(employee == null) {
            throw new NotFoundException("No Employee with the Username exists.");
        }
        Task task = taskService.getTaskById(taskId);
        boolean isAnimalTask = taskService.isAnimalTask(taskId);
        if(isAnimalTask) {
            if(employee.getType() == EmployeeType.DOCTOR) {
                return true;
            } else if(employee.getType() == EmployeeType.ANIMAL_CARE) {
                return employeeService.hasTaskAssignmentPermissions(username,taskId);
            }
        } else {
            if(employee.getType() == EmployeeType.JANITOR) {
                return true;
            } else if(employee.getType() == EmployeeType.ANIMAL_CARE) {

                return employeeService.hasTaskAssignmentPermissions(username,taskId);
            }
        }
        return false;
    }
}
