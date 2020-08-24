package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.entity.TaskComment;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskCommentRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskCommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TaskCommentServiceTest implements TestData {

    @Autowired
    private TaskCommentService taskCommentService;

    @MockBean
    private TaskCommentRepository taskCommentRepository;

    @MockBean
    private TaskRepository taskRepository;

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

    @BeforeEach
    void beforeEach() {
        Mockito.when(taskCommentRepository.findById(taskComment.getId())).thenReturn(java.util.Optional.ofNullable(taskComment));
        Mockito.when(taskRepository.findById(task.getId())).thenReturn(java.util.Optional.ofNullable(task));
        Mockito.when(taskCommentRepository.findAllByTask(task)).thenReturn(Collections.singletonList(taskComment));
        Mockito.when(taskCommentRepository.save(Mockito.any(TaskComment.class))).thenReturn(taskComment);
    }

    @Test
    void findById_whenExisting_returnsComment() {
        assertEquals(taskComment, taskCommentService.findById(taskComment.getId()));
    }

    @Test
    void findById_whenNotExisting_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> taskCommentService.findById(taskComment.getId() + 1));
    }

    @Test
    void findAllByTaskId_whenExisting_returnsListWithTaskComments() {
        assertTrue(taskCommentService.findAllByTaskId(task.getId()).contains(taskComment));
    }

    @Test
    void findAllByTaskId_whenNotExisting_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> taskCommentService.findAllByTaskId(task.getId() + 1));
    }

    @Test
    void delete_whenNotExisting_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> taskCommentService.delete(taskComment.getId() + 1));
    }

}
