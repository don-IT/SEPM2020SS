package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinedTaskDto {

    public interface ValidRepeatUpdate {
        //validation group marker interface
    }

    private Long id;

    @NotNull(message = "Title must not be null", groups = {ValidRepeatUpdate.class})
    @NotBlank(message = "Title must not be empty", groups = {ValidRepeatUpdate.class})
    private String title;

    @NotNull(message = "Description must not be null", groups = {ValidRepeatUpdate.class})
    @NotBlank(message = "Description must not be empty", groups = {ValidRepeatUpdate.class})
    private String description;

    @NotNull(message = "startTime must not be null")
    @FutureOrPresent(message = "Task cant start in the past")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "Name must not be null")
    @FutureOrPresent(message = "Task cant end in the past")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private String assignedEmployeeUsername;

    @NotNull(message = "Task Status must not be null")
    private TaskStatus status;

  /*  @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be empty")*/
    private String subjectName;

    @NotNull(message = "Name must not be null", groups = {ValidRepeatUpdate.class})
    private Long subjectId;

    @NotNull(message = "You have to specify the kind of task", groups = {ValidRepeatUpdate.class})
    private boolean animalTask;

    @NotNull(message = "You have to specify the priority", groups = {ValidRepeatUpdate.class})
    private boolean priority;

    @NotNull(message = "You have to specify the if this Task is an Event", groups = {ValidRepeatUpdate.class})
    private boolean event;

    private String publicInfo;

    @Pattern(regexp = "^data:image/(jpeg|png);base64,([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$",
        message = "Event Picture needs to be a valid jpg or png image")
    private String eventPicture;
}
