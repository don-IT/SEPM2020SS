package at.ac.tuwien.sepm.groupphase.backend.unittests.mapper;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TaskCommentDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TaskCommentMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.entity.TaskComment;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TaskCommentMappingTest implements TestData {

    @Autowired
    private TaskCommentMapper taskCommentMapper;

    private UserLogin creator = UserLogin.builder()
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .build();

    private Task task = Task.builder()
        .id(2L)
        .build();

    private TaskComment taskComment = TaskComment.builder()
        .id(2L)
        .comment(COMMENT)
        .task(task)
        .creator(creator)
        .timestamp(LocalDateTime.now())
        .build();

    private TaskCommentDto taskCommentDto = TaskCommentDto.builder()
        .id(2L)
        .comment(COMMENT)
        .taskId(task.getId())
        .creatorUsername(creator.getUsername())
        .timeStamp(taskComment.getTimestamp())
        .build();

    @Test
    public void taskCommentToTaskCommentDto() {
        assertEquals(taskCommentDto, taskCommentMapper.taskCommentToTaskCommentDto(taskComment));
    }

    @Test
    public void taskCommentDtoToTaskComment() {
        assertEquals(taskComment, taskCommentMapper.taskCommentDtoToTaskComment(taskCommentDto));
    }
}
