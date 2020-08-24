package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TaskCommentDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Task;
import at.ac.tuwien.sepm.groupphase.backend.entity.TaskComment;
import at.ac.tuwien.sepm.groupphase.backend.entity.UserLogin;
import org.springframework.stereotype.Component;

@Component
public class TaskCommentMapper {

    public TaskComment taskCommentDtoToTaskComment(TaskCommentDto taskCommentDto) {
        return TaskComment.builder()
            .id(taskCommentDto.getId())
            .comment(taskCommentDto.getComment())
            .creator(UserLogin.builder().username(taskCommentDto.getCreatorUsername()).build())
            .task(Task.builder().id(taskCommentDto.getId()).build())
            .timestamp(taskCommentDto.getTimeStamp())
            .build();
    }

    public TaskCommentDto taskCommentToTaskCommentDto(TaskComment taskComment) {
        return TaskCommentDto.builder()
            .id(taskComment.getId())
            .comment(taskComment.getComment())
            .creatorUsername(taskComment.getCreator().getUsername())
            .taskId(taskComment.getTask().getId())
            .timeStamp(taskComment.getTimestamp())
            .build();
    }

}
