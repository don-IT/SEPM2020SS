package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalTask {

    @Id
    private Long id;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Task task;

    @NonNull
    @ManyToOne(fetch=FetchType.EAGER)
    private Animal subject;
}
