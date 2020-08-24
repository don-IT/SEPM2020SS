package at.ac.tuwien.sepm.groupphase.backend.unittests.service;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.exception.DeletionException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EnclosureService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class EnclosureServiceTest implements TestData {

    @Autowired
    EnclosureService enclosureService;

    @MockBean
    EnclosureRepository enclosureRepository;

    @MockBean
    AnimalRepository animalRepository;

    private Enclosure enclosureDetailed = Enclosure.builder()
        .name(NAME_LION_ENCLOSURE)
        .description(DESCRIPTION_LION_ENCLOSURE)
        .publicInfo(PUBLIC_INFO_LION_ENCLOSURE)
        .picture(PICTURE_LION_ENCLOSURE)
        .build();

    private Enclosure enclosureMinimal = Enclosure.builder()
        .name("Wolf Enclosure")
        .description(null)
        .publicInfo(null)
        .picture(null)
        .build();

    private Enclosure enclosureMinimal2 = Enclosure.builder()
        .id(1L)
        .name("Wolf Enclosure")
        .description(null)
        .publicInfo(null)
        .picture(null)
        .build();

    private Animal animal = Animal.builder()
        .id(2L)
        .name("Brandy")
        .description("racing Horce")
        .enclosure(null)
        .species("race")
        .publicInformation(null)
        .build();

    @BeforeEach
    public void beforeEach() {
        enclosureRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        enclosureRepository.deleteAll();
    }

    @Test
    public void filledRepository_whenFindAll_thenListOfAllEnclosures() {

        List<Enclosure> enclosures = new LinkedList<>();
        enclosures.add(enclosureDetailed);
        enclosures.add(enclosureMinimal);

        Mockito.when(enclosureRepository.findAll()).thenReturn(enclosures);
        assertTrue(enclosureService.getAll().size()==2);
    }


    @Test
    public void EmptyRepository_whenFindAll_thenEmptyListOfAllEnclosures() {

        List<Enclosure> enclosures = new LinkedList<>();

        Mockito.when(enclosureRepository.findAll()).thenReturn(enclosures);
        assertTrue(enclosureService.getAll().size()==0);
    }


    @Test
    public void saveEnclosurebyGivingOnlyMandatoryValues() {

        Mockito.when(enclosureService.create(enclosureMinimal)).thenReturn(enclosureMinimal);

        assertAll(
            () -> assertEquals(enclosureMinimal, enclosureService.create(enclosureMinimal))
        );
    }

    @Test
    public void saveEnclosurebyGivingAllValues() {

        Mockito.when(enclosureService.create(enclosureDetailed)).thenReturn(enclosureDetailed);

        assertAll(
            () -> assertEquals(enclosureDetailed, enclosureService.create(enclosureDetailed))
        );
    }

    @Test
    public void deleteEnclosureWithoutAssignedAnimals() {
        Mockito.when(enclosureService.findById(enclosureMinimal2.getId())).thenReturn(enclosureMinimal2);
        Enclosure enclosure = enclosureService.findById(enclosureMinimal2.getId());
        enclosureService.deleteEnclosure(enclosure.getId());
        List<Enclosure> enclosures = enclosureService.getAll();
        assertEquals(0, enclosures.size());
    }

    @Test
    public void deleteEnclosureWithAssignedAnimals() {
        List<Animal> animals= new LinkedList<>();
        animals.add(animal);
        Mockito.when(enclosureService.findById(enclosureMinimal2.getId())).thenReturn(enclosureMinimal2);
        Mockito.when(animalRepository.findAllByEnclosure(enclosureMinimal2)).thenReturn(animals);
        Enclosure enclosure = enclosureService.findById(enclosureMinimal2.getId());
        assertThrows(DeletionException.class, ()->{enclosureService.deleteEnclosure(enclosure.getId());});
    }

}
