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

public class EnclosureTask {

    @Id
    private Long id;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Task task;


    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    private Enclosure subject;
}
