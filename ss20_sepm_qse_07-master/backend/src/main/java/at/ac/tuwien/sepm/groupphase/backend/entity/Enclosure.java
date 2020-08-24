package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = "animals")
@ToString(exclude = "animals")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enclosure {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column
    private String description;

    @Lob
    @Column
    private String publicInfo;

    @Lob
    @Column
    private byte[] picture;

    @OneToMany(mappedBy = "enclosure")
    private List<Animal> animals;

    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<EnclosureTask> tasks;

    public Enclosure(Long id, String name, String description, String publicInfo, byte[] picture) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.publicInfo = publicInfo;
        this.picture = picture;
    }

    public Enclosure(Enclosure enclosure) {
        this.id = enclosure.id;
        this.name = enclosure.name;
        this.description = enclosure.description;
        this.publicInfo = enclosure.publicInfo;
        this.picture = enclosure.picture;
        this.animals = enclosure.animals;
        this.tasks = enclosure.tasks;
    }
}

