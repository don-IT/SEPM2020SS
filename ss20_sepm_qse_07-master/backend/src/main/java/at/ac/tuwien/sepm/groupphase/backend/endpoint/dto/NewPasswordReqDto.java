package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewPasswordReqDto {
    @NotBlank(message = "Username must not be empty")
    @Size(max = 255, message = "Username can be at most 255 characters")
    private String username;

    private String currentPassword;

    @NotNull(message = "Password must not be null")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$",
        message = "Password needs to contain at least one uppercase letter, one lowercase letter, a number and a minimum of 8 characters")
    @Size(max = 255, message = "Password can be at most 255 characters")
    private String newPassword;
}
