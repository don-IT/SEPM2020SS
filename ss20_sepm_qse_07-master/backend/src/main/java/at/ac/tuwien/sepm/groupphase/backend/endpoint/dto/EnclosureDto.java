package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnclosureDto {

    @NotBlank(message = "name must not be empty")
    private String name;

    private Long id;

    private String description;

    private String publicInfo;

    @Pattern(regexp = "^data:image/(jpeg|png);base64,([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$",
        message = "picture needs to be a valid jpg or png image")
    private String picture;
}
