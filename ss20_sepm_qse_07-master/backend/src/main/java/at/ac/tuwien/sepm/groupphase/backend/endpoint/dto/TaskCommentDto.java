package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCommentDto {
    private Long id;

    @NotNull(message = "Comment must not be null")
    @NotBlank(message = "Comment must not be empty")
    private String comment;

    private String creatorUsername;

    @NotNull(message = "Comment must belong to a task")
    private Long taskId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeStamp;
}
