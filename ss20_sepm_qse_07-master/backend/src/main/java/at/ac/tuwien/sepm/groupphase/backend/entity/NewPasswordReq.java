package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewPasswordReq {
    private String username;
    private String currentPassword;
    private String newPassword;
}
