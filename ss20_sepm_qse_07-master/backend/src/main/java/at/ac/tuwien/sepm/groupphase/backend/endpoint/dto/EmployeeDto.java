package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    @NotBlank(message = "Username must not be empty")
    @Size(max = 255, message = "Username can be at most 255 characters")
    private String username;

    @Email(message = "Email must be a valid E-Mail-Address")
    @Size(max = 255, message = "Email can be at most 255 characters")
    private String email;

    @NotNull(message = "Password must not be null")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$",
        message = "Password needs to contain at least one uppercase letter, one lowercase letter, a number and a minimum of 8 characters")
    @Size(max = 255, message = "Password can be at most 255 characters")
    private String password;

    @NotBlank(message = "Name must not be empty")
    @Size(max = 255, message = "Name can be at most 255 characters")
    private String name;

    @NotNull(message = "Birthdate must not be null")
    @PastOrPresent(message = "Birthday can't be in the future")
    private Date birthday;

    @NotNull(message = "Type must not be null")
    private EmployeeType type;

    @NotNull(message = "work time start must not be null")
    private LocalTime workTimeStart;

    @NotNull(message = "work time end must not be null")
    private LocalTime workTimeEnd;
}