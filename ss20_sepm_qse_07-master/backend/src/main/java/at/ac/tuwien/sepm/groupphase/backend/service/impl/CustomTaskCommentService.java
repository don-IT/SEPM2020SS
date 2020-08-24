package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.entity.TaskComment;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskCommentRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TaskRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomTaskCommentService implements TaskCommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TaskRepository taskRepository;

    private final TaskCommentRepository taskCommentRepository;

    @Autowired
    public CustomTaskCommentService(TaskRepository taskRepository, TaskCommentRepository taskCommentRepository) {
        this.taskRepository = taskRepository;
        this.taskCommentRepository = taskCommentRepository;
    }

    @Override
    public TaskComment findById(Long taskCommentId) {
        LOGGER.debug("find TaskComment with id {}", taskCommentId);
        Optional<TaskComment> taskComment = taskCommentRepository.findById(taskCommentId);
        if(taskComment.isEmpty()) {
            throw new NotFoundException("No task with the id " + taskCommentId + " exists");
        } else {
            return taskComment.get();
        }
    }

    @Override
    public List<TaskComment> findAllByTaskId(Long taskId) {
        LOGGER.debug("find all TaskComments of task with id {}", taskId);
        Optional<Task> task = taskRepository.findById(taskId);
        if(task.isEmpty()) {
            throw new NotFoundException("No task with the id " + taskId + " exists");
        } else {
            return taskCommentRepository.findAllByTask(task.get());
        }
    }

    @Override
    public TaskComment createComment(TaskComment taskComment) {
        LOGGER.debug("create new TaskComment: {}", taskComment);

        taskComment.setTimestamp(LocalDateTime.now());
        return taskCommentRepository.save(taskComment);
    }

    @Override
    public void delete(Long taskCommentId) {
        LOGGER.debug("delete TaskComment with id {}", taskCommentId);
        Optional<TaskComment> taskComment = taskCommentRepository.findById(taskCommentId);
        if(taskComment.isEmpty()) {
            throw new NotFoundException("No task with the id " + taskCommentId + " exists");
        } else {
            taskCommentRepository.delete(taskComment.get());
        }
    }
}
