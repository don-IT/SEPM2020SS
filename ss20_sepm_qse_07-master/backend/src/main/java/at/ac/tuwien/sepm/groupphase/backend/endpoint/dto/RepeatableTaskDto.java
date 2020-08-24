package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepeatableTaskDto {

    private Long id;

    @NotNull(message = "Title must not be null")
    @NotBlank(message = "Title must not be empty")
    private String title;

    @NotNull(message = "Description must not be null")
    @NotBlank(message = "Description must not be empty")
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

    private TaskStatus status;

    @NotNull
    private boolean priority;

    @Positive
    private int amount;

    @NotNull
    private ChronoUnit separation;

    @Positive
    private int separationAmount;

    @NotNull
    private boolean event;

    private String publicInfo;

    @Pattern(regexp = "^data:image/(jpeg|png);base64,([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$",
        message = "Event Picture needs to be a valid jpg or png image")
    private String eventPicture;

}
