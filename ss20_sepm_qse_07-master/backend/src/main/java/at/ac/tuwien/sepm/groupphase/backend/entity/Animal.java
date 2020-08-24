package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"caretakers"})
@ToString(exclude = {"caretakers"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Animal implements Serializable {

    @Column(nullable = false)
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String species;

    @ManyToOne(fetch = FetchType.EAGER)
    private Enclosure enclosure;

    @Column
    private String publicInformation;

    @ManyToMany(mappedBy = "assignedAnimals")
    private List<Employee> caretakers;

    @OneToMany(mappedBy = "subject", fetch = FetchType.EAGER)
    private List<AnimalTask> tasks;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Animal)) return false;
        Animal animal = (Animal) o;
        return getId().equals(animal.getId());
    }

}
