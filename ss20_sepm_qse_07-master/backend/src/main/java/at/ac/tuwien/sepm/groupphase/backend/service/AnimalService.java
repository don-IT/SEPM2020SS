package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;

import java.util.List;

public interface AnimalService {

    /**
     * Creates an single animal.
     * @param animal to save.
     * @return saved animal.
     */
    Animal saveAnimal(Animal animal);


    /**
     * Method to get all current animals
     * @return a List of all current animals
     */
    List<Animal> getAll();

    /**
     * Deletes an single animal.
     * @param id of animal to be deleted.
     */
    void deleteAnimal(Long id);


    /**
     * finds an single animal.
     * @param id of animal to be deleted.
     * @return
     */
    Animal findAnimalById(Long id);

    /**
     * Assign an animal to an enclosure
     *
     * @param animal that will get assigned to the Enclosure
     * @param enclosureId that the Animal will get assigned to
     * @return animal with the assigned Enclosure
     */
    Animal addAnimalToEnclosure(Animal animal, long enclosureId);

    /**
     * Remove an animal from its enclosure
     *
     * @param animal that will be removed from its enclosure
     * @return animal without the enclosure reference
     */
    Animal removeAnimalFromEnclosure(Animal animal);

    /**
     * Finds all Animals of a specific Enclosure
     *
     * @param enclosureId of the Enclosure inhabited by the requested Animals
     * @return Animals that inhabit the Enclosure with the corresponding Id
     */
    List<Animal> findAnimalsByEnclosure(long enclosureId);

    /**
     * Method for filtered list of all current animals or all animals of one enclosure, search fields can be combined.
     * If a field is null it is not taken into consideration
     *
     * @param animal with saved properties for search
     * @return animals that fulfill the criterums.
     */
    List<Animal> searchAnimals(Animal animal);

    /**
     * Method for filtered list of all animals of one employee, search fields can be combined.
     * If a field is null it is not taken into consideration
     *
     * @param animal with saved properties for search
     * @param username of employee
     * @return animals that fulfill the criterums.
     */
    List<Animal> searchAnimalsOfEmployee(Animal animal, String username);


    /**
     * Editing Animal that is already in the Database
     *
     * @param animal to be edited
     * @return edited Animal as saved in the Database
     */
    Animal editAnimal(Animal animal);
}
