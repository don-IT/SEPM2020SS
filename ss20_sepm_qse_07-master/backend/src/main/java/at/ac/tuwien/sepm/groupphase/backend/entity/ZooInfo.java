package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = "ZooInfo")
@ToString(exclude = "ZooInfo")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZooInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String name;

    @Lob
    @Column
    private String address;

    @Lob
    @Column
    private String publicInfo;

    @Lob
    @Column
    private byte[] picture;

    @Column
    private LocalTime workTimeStart;

    @Column
    private LocalTime workTimeEnd;

    public ZooInfo(ZooInfo zooInfo) {
        this.name = zooInfo.name;
        this.address = zooInfo.address;
        this.publicInfo = zooInfo.publicInfo;
        this.picture = zooInfo.picture;
        this.workTimeStart = zooInfo.workTimeStart;
        this.workTimeEnd = zooInfo.workTimeEnd;
    }

}
