package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Animal;
import at.ac.tuwien.sepm.groupphase.backend.entity.Employee;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {


    /**
     * Finds an employee with a specific username
     *
     * @param username of the employee to be found
     * @return employee with the corresponding username
     */
    Employee findEmployeeByUsername(String username);

    /**
     * Method to get all employees
     * @return List containing all Employees
     */
    List<Employee> findAll();


    /**
     * Get Employee by Example
     * @return List containing all Employees that match the Example
     */
    <S extends Employee> List<S> findAll(Example<S> example);

    /**
     *
     * @param animal is the searched for animal
     * @return List of Employees that are assigned to given animal
     */
    List<Employee> findByAssignedAnimalsContains(Animal animal);

    /**
     *
     * @return List of Employees that are assigned to some animal
     */
    //List<Employee> findEmployeesByAssignedAnimalsContains();

    /**
     *
     * @param enclosureIdLong is the id of the enclosure we want to get the assigned employees from
     * @return List of Employees that are assigned to given enclosure
     */
    @Query(value = "SELECT * " +
        "FROM EMPLOYEE  e WHERE e.USERNAME IN " +
        "(SELECT ac.EMPLOYEE_USERNAME " +
        "FROM ANIMALS_CARETAKERS ac " +
        "INNER JOIN ANIMAL a ON ac.ANIMAL_ID = a.ID " +
        "WHERE a.ENCLOSURE_ID =:enclosureId)",
        nativeQuery = true)
    List<Employee> getEmployeesByEnclosureID(@Param("enclosureId")Long enclosureIdLong);


    /**
     * Method to get all employees of specific Type
     * @return List containing all Employees of specific Type
     */
    List<Employee> findAllByType(EmployeeType type);


    @Transactional
    @Modifying
    @Query(value = "delete from ANIMALS_CARETAKERS a where a.EMPLOYEE_USERNAME = :username and a.ANIMAL_ID= :animalId",
        nativeQuery = true)
    void removeAssignedAnimal (@Param("username") String username, @Param("animalId") Long animalId);
}
