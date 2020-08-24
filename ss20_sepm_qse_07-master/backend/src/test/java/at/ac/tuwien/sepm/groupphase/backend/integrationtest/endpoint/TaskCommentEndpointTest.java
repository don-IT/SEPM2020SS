package at.ac.tuwien.sepm.groupphase.backend.integrationtest.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TaskCommentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TaskCommentMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskCommentRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserLoginRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TaskCommentEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private TaskCommentRepository taskCommentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskCommentMapper taskCommentMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    private UserLogin admin = UserLogin.builder()
        .username(ADMIN_USER)
        .password("pw")
        .isAdmin(true).build();

    private Task task = Task.builder()
        .title(TASK_TITLE)
        .description(TASK_DESCRIPTION)
        .startTime(TAST_START_TIME)
        .endTime(TAST_END_TIME)
        .status(TaskStatus.NOT_ASSIGNED)
        .priority(false)
        .build();

    private TaskComment taskComment;

    @BeforeEach
    void beforeEach() {
        taskCommentRepository.deleteAll();
        userLoginRepository.deleteAll();
        taskRepository.deleteAll();
        task = taskRepository.save(task);
        admin = userLoginRepository.save(admin);
        taskComment = taskCommentRepository.save(TaskComment.builder().creator(admin).comment(COMMENT).task(task).timestamp(LocalDateTime.now()).build());
    }

    @AfterEach
    void afterEach() {
        taskCommentRepository.deleteAll();
        userLoginRepository.deleteAll();
        taskRepository.deleteAll();
        task = Task.builder()
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .startTime(TAST_START_TIME)
            .endTime(TAST_END_TIME)
            .status(TaskStatus.NOT_ASSIGNED)
            .priority(false)
            .build();
        admin = UserLogin.builder()
            .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
            .password("pw")
            .isAdmin(true).build();
    }

    @Test
    public void getCommentsOfTask_returnsListOfTaskCommentDtos() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(TASKCOMMENT_BASE_URI + "/" + task.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        List<TaskCommentDto> taskCommentDtoList = objectMapper.readValue(response.getContentAsString(),
            new TypeReference<>() {
            });

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertEquals(1, taskCommentDtoList.size()),
            () -> assertEquals(taskComment.getId(), taskCommentDtoList.get(0).getId()),
            () -> assertEquals(taskComment.getComment(), taskCommentDtoList.get(0).getComment()),
            () -> assertEquals(taskComment.getTask().getId(), taskCommentDtoList.get(0).getTaskId()),
            () -> assertEquals(taskComment.getCreator().getUsername(), taskCommentDtoList.get(0).getCreatorUsername()),
            () -> assertEquals(taskComment.getTimestamp().truncatedTo(ChronoUnit.SECONDS), taskCommentDtoList.get(0).getTimeStamp())
        );
    }

    @Test
    public void getCommentsOfTask_whenTaskNotExisting_returnStatusNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(TASKCOMMENT_BASE_URI + "/" + task.getId() + 1)
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void deleteComment_whenCommentExisting_returnStatusOK() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(TASKCOMMENT_BASE_URI + "/" + taskComment.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void deleteComment_whenCommentNotExisting_returnStatusNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(TASKCOMMENT_BASE_URI + "/" + taskComment.getId() + 1)
            .contentType(MediaType.APPLICATION_JSON)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void createComment_whenValid_returnComment() throws Exception {
        String body = objectMapper.writeValueAsString(taskCommentMapper.taskCommentToTaskCommentDto(taskComment));

        MvcResult mvcResult = this.mockMvc.perform(post(TASKCOMMENT_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        TaskCommentDto taskCommentDto = objectMapper.readValue(response.getContentAsString(),
            TaskCommentDto.class);

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType()),
            () -> assertEquals(taskComment.getId(), taskCommentDto.getId()),
            () -> assertEquals(taskComment.getComment(), taskCommentDto.getComment()),
            () -> assertEquals(taskComment.getTask().getId(), taskCommentDto.getTaskId()),
            () -> assertEquals(taskComment.getCreator().getUsername(), taskCommentDto.getCreatorUsername())
        );
    }

    @Test
    public void createComment_whenInvalid_returnStatusBadRequest() throws Exception {
        TaskCommentDto taskCommentDto = taskCommentMapper.taskCommentToTaskCommentDto(taskComment);
        taskCommentDto.setComment(null);

        String body = objectMapper.writeValueAsString(taskCommentDto);

        MvcResult mvcResult = this.mockMvc.perform(post(TASKCOMMENT_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }
}

