package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString(exclude = {"caretakers"})
@EqualsAndHashCode(exclude = {"caretakers"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalDto {

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be empty")
    private String name;

    private Long id;

    @NotNull(message = "Description must not be null")
    @NotBlank(message = "Description must not be empty")
    private String description;

    @NotNull(message = "Species must not be null")
    @NotBlank(message = "Species must not be empty")
    private String species;

    private String publicInformation;

    private List<Employee> caretakers;
}