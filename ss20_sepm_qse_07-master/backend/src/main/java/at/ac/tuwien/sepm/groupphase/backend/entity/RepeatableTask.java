package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepeatableTask {

    @Id
    private Long id;

    @OneToOne(optional = false)
    @PrimaryKeyJoinColumn
    private Task task;

    @OneToOne
    private Task followTask;
}
