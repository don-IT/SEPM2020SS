package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AnimalDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import org.springframework.stereotype.Component;

@Component
public class AnimalMapper {

    public AnimalDto animalToAnimalDto(Animal animal) {
        if(animal == null) {
            return null;
        }

        return AnimalDto.builder().id(animal.getId())
            .id(animal.getId())
            .name(animal.getName())
            .description(animal.getDescription())
            .species(animal.getSpecies())
            .publicInformation(animal.getPublicInformation())
            .build();
    }

    public Animal AnimalDtoToAnimal(AnimalDto animalDto) {
        if(animalDto == null) {
            return null;
        }

        return Animal.builder().id(animalDto.getId())
            .id(animalDto.getId())
            .name(animalDto.getName())
            .description(animalDto.getDescription())
            .species(animalDto.getSpecies())
            .publicInformation(animalDto.getPublicInformation())
            .caretakers(animalDto.getCaretakers())
            .build();
    }
}
