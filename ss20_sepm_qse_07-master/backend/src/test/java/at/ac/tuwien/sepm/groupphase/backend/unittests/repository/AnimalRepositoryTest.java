package at.ac.tuwien.sepm.groupphase.backend.unittests.repository;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class AnimalRepositoryTest implements TestData {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private EnclosureRepository enclosureRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private final Enclosure enclosure = Enclosure.builder()
        .name(NAME_LION_ENCLOSURE)
        .description(DESCRIPTION_LION_ENCLOSURE)
        .publicInfo(PUBLIC_INFO_LION_ENCLOSURE)
        .picture(PICTURE_LION_ENCLOSURE)
        .build();

    private Employee animal_caretaker = Employee.builder()
        .username(USERNAME_ANIMAL_CARE_EMPLOYEE)
        .name(NAME_ANIMAL_CARE_EMPLOYEE)
        .birthday(BIRTHDAY_ANIMAL_CARE_EMPLOYEE)
        .type(TYPE_ANIMAL_CARE_EMPLOYEE)
        .email(EMAIL_ANIMAL_CARE_EMPLOYEE)
        .workTimeStart(TEST_LOCAL_TIME_START)
        .workTimeEnd(TEST_LOCAL_TIME_END)
        .build();

    private final Animal animal = Animal.builder()
        .name("Brandy")
        .description("racing Horse")
        .enclosure(null)
        .species("race")
        .publicInformation(null)
        .build();

    @BeforeEach
    public void beforeEach() {
        animalRepository.deleteAll();
        enclosureRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        animalRepository.deleteAll();
        enclosureRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    public void saveAnimalbyGivingOnlyMandatoryValues_thenFindAnimalById() {
        long animalId = animalRepository.save(animal).getId();

        assertNotNull(animalRepository.findById(animalId));
    }

    @Test
    public void findById_whenNotExistingId_thenNull() {
        long animalId = animalRepository.save(animal).getId();

        assertNull(animalRepository.findById(animalId + 1));
    }

    @Test
    public void emptyRepository_whenFindAll_thenEmptyList() {
        List<Animal> animals = animalRepository.findAll();
        assertEquals(0, animals.size());
    }

    @Test
    public void ReturnsOneAnimal_whenSaveAnimal_thenFindAll_() {
        Animal savedAnimal = animalRepository.save(animal);

        List<Animal> animals = animalRepository.findAll();
        assertTrue(animals.contains(savedAnimal));
    }

    @Test
    public void emptyRepository_whenFindByEnclosure_thenEmptyList() {
        Enclosure savedEnclosure = enclosureRepository.save(enclosure);
        List<Animal> animals = animalRepository.findAllByEnclosure(savedEnclosure);
        assertTrue(animals.isEmpty());
    }

    @Test
    public void findByEnclosure_whenAnimalsAssignedToEnclosure_returnsCorrectAnimals() {
        Enclosure savedEnclosure = enclosureRepository.save(enclosure);

        Animal animal2 = Animal.builder()
            .name("Potoooooooo")
            .description("legendary")
            .enclosure(savedEnclosure)
            .species("Horse_PAINT")
            .publicInformation(null)
            .build();

        Animal savedAnimal1 = animalRepository.save(animal);
        Animal savedAnimal2 = animalRepository.save(animal2);

        List<Animal> animals = animalRepository.findAllByEnclosure(savedEnclosure);

        assertFalse(animals.contains(savedAnimal1));
        assertTrue(animals.contains(savedAnimal2));
    }

    @Test
    public void findByDescription_whenNoDescriptionsMatch_thenEmptyList() {
        animalRepository.save(animal);
        List<Animal> animals = animalRepository.findByDescription("no description");
        assertTrue(animals.isEmpty());
    }

    @Test
    public void findByDescription_whenDescriptionMatches_thenAnimalInList() {
        Animal savedAnimal = animalRepository.save(animal);
        List<Animal> animals = animalRepository.findByDescription(savedAnimal.getDescription());
        assertTrue(animals.contains(savedAnimal));
    }

    @Test
    public void assignAnimalToCaretaker_whenFindByCaretaker_returnsAnimal() {
        Employee savedEmployee = employeeRepository.save(animal_caretaker);
        Animal savedAnimal = animalRepository.save(animal);
        animalRepository.assignAnimalToCaretaker(savedEmployee.getUsername(), savedAnimal.getId());
        List<Animal> animals = animalRepository.findAllByCaretakers(savedEmployee);
        assertTrue(animals.contains(savedAnimal));
    }

    @Test void deleteAssignmentToCaretaker_whenFindByCaretaker_returnsEmptyList() {
        Employee savedEmployee = employeeRepository.save(animal_caretaker);
        Animal savedAnimal = animalRepository.save(animal);
        animalRepository.assignAnimalToCaretaker(savedEmployee.getUsername(), savedAnimal.getId());
        animalRepository.deleteAssignmentsOfAnimal(savedAnimal.getId());
        List<Animal> animals = animalRepository.findAllByCaretakers(savedEmployee);
        assertTrue(animals.isEmpty());
    }
}
