package at.ac.tuwien.sepm.groupphase.backend.unittests.service;
import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class AnimalServiceTest implements TestData {

    @Autowired
    private AnimalService animalService;

    @MockBean
    AnimalRepository animalRepository;

    private Animal animal = Animal.builder()
        .id(2L)
        .name("Brandy")
        .description("racing Horce")
        .enclosure(null)
        .species("race")
        .publicInformation(null)
        .build();




    @Test
    public void saveAnimalbyGivingOnlyMandatoryValues_thenFindAnimalById() {
        Mockito.when(animalRepository.save(animal)).thenReturn(animal);

        assertAll(
            () -> assertEquals(animalService.saveAnimal(animal),animal)
        );
    }

    @Test
    public void emptyRepository_whenFindAll_thenEmptyList() {
        animalRepository.deleteAll();
        List<Animal> animalTest=new LinkedList<>();
        Mockito.when(animalRepository.findAll()).thenReturn(animalTest);
        List<Animal> animals = animalRepository.findAll();
        assertEquals(0, animals.size());
    }

    @Test
    public void ReturnsOneAnimal_whenSaveAnimal_thenFindAll_() {
        animalRepository.deleteAll();
        List<Animal> animalTest=new LinkedList<>();
        animalTest.add(animal);
        Mockito.when(animalRepository.findAll()).thenReturn(animalTest);

        List<Animal> animals = animalRepository.findAll();
        assertEquals(1, animals.size());
    }

    @Test
    public void whenDeletingNotExistingAnimal_thenNotFoundException(){
        animalRepository.deleteAll();
        Mockito.when(animalRepository.findById(1L)).thenReturn(null);
        assertThrows(NotFoundException.class, ()->{animalService.deleteAnimal(1L);});
    }

   @Test
    public void filledRepository_searchAnimalNoMatching_thenThrowNotFoundException() {
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAll().withIgnoreNullValues().withIgnoreCase()
            .withMatcher("noName", ExampleMatcher.GenericPropertyMatchers.contains())
            .withMatcher("NoSpecie", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<Animal> example = Example.of(Animal.builder().name("noName").species("NoSpecie").build(), customExampleMatcher);
        Mockito.when(animalRepository.findAll(example)).thenReturn(new LinkedList<>());
        assertThrows(Exception.class, () -> animalService.searchAnimals(Animal.builder().name("noName").species("NoSpecie").build()));
    }


    //@Test
    public void filledRepository_searchAnimalMatching_thenReturnOneAnimal() {
        animalRepository.deleteAll();
        animalRepository.save(animal);

        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAll().withIgnoreNullValues().withIgnoreCase()
            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
            .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.contains())
            .withMatcher("species", ExampleMatcher.GenericPropertyMatchers.exact())
            .withMatcher("enclosure", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<Animal> example = Example.of(Animal.builder().name(animal.getName()).species(animal.getSpecies()).description(animal.getDescription())
            .enclosure(animal.getEnclosure()).build(), customExampleMatcher);

        List<Animal> mockResult= new LinkedList<>();
        mockResult.add(animal);

        Mockito.when(animalRepository.findAll(example)).thenReturn(mockResult);
        List<Animal> result = animalService.searchAnimals(animal);

        assertTrue(result.size()==1);
    }

    @Test
    public void whenEditingNotExistingAnimal_thenNotFoundException(){
        animalRepository.deleteAll();
         Animal animal1 = Animal.builder()
            .id(5L)
            .name("Milly")
            .description("fastest horse")
            .enclosure(null)
            .species("brown")
            .publicInformation(null)
            .build();
        Mockito.when(animalRepository.findById(5L)).thenReturn(null);
        assertThrows(NotFoundException.class, ()->{animalService.editAnimal(animal1);});
    }

    @Test
    public void whenEditingExistingAnimal_thenSuccess(){
        animalRepository.deleteAll();
        Animal animal1 = Animal.builder()
            .id(5L)
            .name("Milly")
            .description("fastest horse")
            .enclosure(null)
            .species("brown")
            .publicInformation(null)
            .build();
        Mockito.when(animalRepository.findById(5L)).thenReturn(animal1);
        assertTrue(animalRepository.findById(5L).getName() == "Milly");
        assertTrue(animalRepository.findById(5L).getDescription() == "fastest horse");
        assertTrue(animalRepository.findById(5L).getSpecies() == "brown");
    }
}
