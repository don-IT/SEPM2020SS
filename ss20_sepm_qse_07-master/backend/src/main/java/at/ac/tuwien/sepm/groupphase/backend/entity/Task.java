package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import lombok.*;
import org.mapstruct.Mapper;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime startTime;

    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime endTime;

    @ManyToOne(fetch=FetchType.EAGER)
    private Employee assignedEmployee;

    @Column(nullable = false)
    private TaskStatus status;

    @Column(nullable = false)
    private boolean priority;

    @Column(nullable = false)
    private boolean event;

    @Lob
    @Column
    private String publicInfo;

    @Lob
    @Column
    private byte[] eventPicture;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TaskComment> taskComments;
}
