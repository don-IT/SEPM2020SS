package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.entity.Enclosure;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.AnimalRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EmployeeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EnclosureRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class SimpleAnimalService implements AnimalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AnimalRepository animalRepository;
    private final EnclosureRepository enclosureRepository;
    private final EmployeeRepository employeeRepository;


    @Autowired
    public SimpleAnimalService(AnimalRepository animalRepository, EnclosureRepository enclosureRepository, EmployeeRepository employeeRepository) {
        this.animalRepository = animalRepository;
        this.enclosureRepository = enclosureRepository;
        this.employeeRepository = employeeRepository;
    }


    @Override
    public Animal saveAnimal(Animal animal) {
        LOGGER.debug("Save new animal {}", animal);
        return animalRepository.save(animal);
    }

    @Override
    public List<Animal> getAll(){
        LOGGER.debug("Getting List of all animals.");
        List<Animal> animals = (List<Animal>) animalRepository.findAll();
        if(animals.isEmpty())
            throw new NotFoundException("There are currently no animals");
        return animals;
    }

    @Override
    public List<Animal> searchAnimals(Animal animal){
        LOGGER.debug("Animal Service: Getting filtered List of animals.");
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAll().withIgnoreNullValues().withIgnoreCase()
            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
            .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.contains())
            .withMatcher("species", ExampleMatcher.GenericPropertyMatchers.exact())
            .withMatcher("enclosure", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<Animal> example = Example.of(Animal.builder().name(animal.getName()).species(animal.getSpecies()).description(animal.getDescription())
            .enclosure(animal.getEnclosure()).build(), customExampleMatcher);
        List<Animal> animalsFiltered = animalRepository.findAll(example);

        if(animalsFiltered.isEmpty())
            throw new NotFoundException("No animal fits the given criteria");
        return animalsFiltered;
    }

    @Override
    public List<Animal> searchAnimalsOfEmployee(Animal animal, String username){
        LOGGER.debug("Animal Service: Getting filtered List of animals.");
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAll().withIgnoreNullValues().withIgnoreCase()
            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
            .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.contains())
            .withMatcher("species", ExampleMatcher.GenericPropertyMatchers.exact())
            .withMatcher("enclosure", ExampleMatcher.GenericPropertyMatchers.exact());


        List <Employee> caretakers= new LinkedList<>();
        caretakers.add(employeeRepository.findEmployeeByUsername(username));
        Example<Animal> example = Example.of(Animal.builder().caretakers(caretakers).name(animal.getName())
            .species(animal.getSpecies()).description(animal.getDescription()).enclosure(animal.getEnclosure()).build(),
            customExampleMatcher);

        List<Animal> animalsFiltered =  animalRepository.findAll(example);

        List<Animal> animalsAssigned = animalRepository.findAllByCaretakers(employeeRepository.findEmployeeByUsername(username));

        if(animalsFiltered.isEmpty())
            throw new NotFoundException("No animal fits the given criteria");
        if(animalsAssigned.isEmpty())
            throw new NotFoundException("No animal is assigned to this employee");

        List<Animal> result = animalsFiltered.stream()
            .distinct()
            .filter(animalsAssigned::contains)
            .collect(Collectors.toList());

        return result;
    }

    @Override
    public void deleteAnimal(Long id){
        LOGGER.debug("Deleting an animal.");
        Optional<Animal> animalOptional = animalRepository.findById(id);

        animalOptional.ifPresentOrElse(animal -> {
            animalRepository.deleteAssignmentsOfAnimal(id);
            animalRepository.delete(animal);
        },()->{throw new NotFoundException("No such animal exists.");});
    }

    @Override
    public Animal findAnimalById(Long id){
        LOGGER.debug("Find an animal by Id.");
        Optional<Animal> animal = animalRepository.findById(id);
        if(animal.isEmpty())
            throw new NotFoundException("Could not find animal with given id");
        return animal.get();
    }

    @Override
    public Animal addAnimalToEnclosure(Animal animal, long enclosureId) {
        LOGGER.debug("Add Enclosure with id {} to animal with id {}", enclosureId, animal.getId());
        Enclosure enclosure = enclosureRepository.findById(enclosureId);
        if(enclosure == null) {
            throw new NotFoundException("Could not assign Animal to Enclosure: No Enclosure with id: " + enclosureId + " in the database");
        }
        Animal savedAnimal = animalRepository.findById((long)animal.getId());
        if(savedAnimal == null) {
            throw new NotFoundException("Could not assign Animal to Enclosure: No Animal with id: " + enclosureId + " in the database");
        }
        savedAnimal.setEnclosure(enclosure);
        return animalRepository.save(savedAnimal);
    }

    @Override
    public Animal removeAnimalFromEnclosure(Animal animal) {
        Animal savedAnimal = animalRepository.findById((long)animal.getId());
        if(savedAnimal == null) {
            throw new NotFoundException("Could not remove Animal from Enclosure: No Animal with id: " + animal.getId() + " in the database");
        }
        savedAnimal.setEnclosure(null);
        return animalRepository.save(savedAnimal);
    }

    @Override
    public List<Animal> findAnimalsByEnclosure(long enclosureId) {
        Enclosure enclosure = enclosureRepository.findById(enclosureId);
        if(enclosure == null) {
            throw new NotFoundException("Could not find Animals: No Enclosure with id: " + enclosureId + " in the database");
        }
        return animalRepository.findAllByEnclosure(enclosure);
    }

    @Override
    public Animal editAnimal(Animal animal){
        Animal animal1 = findAnimalById(animal.getId());
        if(animal1 == null){
            throw new NotFoundException("Can not find animal to edit.");
        }
        if(animal == null) {
            throw new IllegalArgumentException("Animal must not be null");
        } else if(animal.getName() == null || animal.getName().isBlank()) {
            throw new IllegalArgumentException("Name of Animal must not be empty");
        } else if(animal.getSpecies() == null) {
            throw new IllegalArgumentException("Specie of Animal must not be empty");
        } else if(animal.getDescription() == null) {
            throw new IllegalArgumentException("Description of Animal must not be empty");
        }

        animal1.setName(animal.getName());
        animal1.setDescription(animal.getDescription());
        animal1.setSpecies(animal.getSpecies());
        animal1.setPublicInformation(animal.getPublicInformation());

        return animalRepository.save(animal1);

    }

}
