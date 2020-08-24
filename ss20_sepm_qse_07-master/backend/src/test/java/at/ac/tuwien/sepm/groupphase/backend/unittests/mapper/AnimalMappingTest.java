package at.ac.tuwien.sepm.groupphase.backend.unittests.mapper;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.AnimalMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class AnimalMappingTest implements TestData {

    private final Animal animal= Animal.builder()
        .id(1L)
        .name("Horse")
        .description("Fast")
        .enclosure(null)
        .species("race")
        .publicInformation("famous")
        .build();

    private final AnimalDto animalDto= AnimalDto.builder()
        .id(2L)
        .name("Dog")
        .description("Fast")
        .species("race")
        .publicInformation(null)
        .build();

    @Autowired
    private AnimalMapper animalMapper;


    @Test
    public void givenNothing_whenMapAnimalDtoToEntity_thenEntityHasAllProperties() {
        AnimalDto animalDto= animalMapper.animalToAnimalDto(animal);

        assertAll(
            () -> assertEquals(animal.getId(), animalDto.getId()),
            () -> assertEquals(animal.getDescription(), animalDto.getDescription()),
            () -> assertEquals(animal.getName(), animalDto.getName()),
            () -> assertEquals(animal.getPublicInformation(), animalDto.getPublicInformation()),
            () -> assertEquals(animal.getSpecies(), animalDto.getSpecies())
        );
    }

    @Test
    public void givenNothing_whenEntitytoMapAnimalDto_thenEntityHasAllProperties() {
        Animal animal= animalMapper.AnimalDtoToAnimal(animalDto);

        assertAll(
            () -> assertEquals(animal.getId(), animalDto.getId()),
            () -> assertEquals(animal.getDescription(), animalDto.getDescription()),
            () -> assertEquals(animal.getName(), animalDto.getName()),
            () -> assertEquals(animal.getPublicInformation(), animalDto.getPublicInformation()),
            () -> assertEquals(animal.getSpecies(), animalDto.getSpecies())
        );
    }
}