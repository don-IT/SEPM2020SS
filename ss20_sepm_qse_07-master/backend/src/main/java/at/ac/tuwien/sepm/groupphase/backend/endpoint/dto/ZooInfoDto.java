package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ZooInfoDto {

    @NotBlank(message = "name must not be empty")
    @Size(max = 255, message = "Name can be at most 255 characters")
    private String name;

    private Long id;

    private String publicInfo;

    @Size(max = 255, message = "Adress can be at most 255 characters")
    private String address;

    private LocalTime workTimeStart;

    private LocalTime workTimeEnd;

    @Pattern(regexp = "^data:image/(jpeg|png);base64,([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$",
        message = "picture needs to be a valid jpg or png image")
    private String picture;
}
