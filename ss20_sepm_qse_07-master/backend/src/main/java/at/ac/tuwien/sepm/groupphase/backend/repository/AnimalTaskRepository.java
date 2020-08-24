package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;

import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.SqlResultSetMapping;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalTaskRepository extends JpaRepository<AnimalTask, Long> {

    /**
     *Finds all Tasks assigned to an Animal
     * @param animalId id of the animal to find associated tasks from
     * @return The tasks associated with the given animal
     */
    @Query("SELECT new AnimalTask(animT.id, t, animT.subject) " +
        "FROM AnimalTask animT JOIN Task t ON animT.id=t.id WHERE animT.subject.id=:animalId ORDER BY t.startTime")
    List<AnimalTask> findAllAnimalTasksBySubject_Id(@Param("animalId") long animalId);


    /**
     *Search for animalTasks with different Parameters
     */
    @Query("SELECT animaltask " +
        "FROM AnimalTask animaltask " +
        "WHERE animaltask.id IN " +
        "(SELECT a.id FROM AnimalTask a WHERE (:#{#filterTask.title} IS NULL OR " +
        "UPPER(a.task.title) LIKE CONCAT('%', UPPER(:#{#filterTask.title}), '%')) " +
        "AND (:#{#filterTask.description} IS NULL OR " +
        "UPPER(a.task.description) LIKE CONCAT('%', UPPER(:#{#filterTask.description}), '%')) " +
        "AND (:#{#filterTask.assignedEmployee.username} IS NULL) " +
        "AND (:#{#employeeType} IS NULL) " +
        "AND ((:#{#filterTask.status} IS NULL) OR " +
        "(a.task.status = :#{#filterTask.status}))) " +
        "OR animaltask.id IN " +
        "(SELECT a.id FROM AnimalTask a WHERE (:#{#filterTask.title} IS NULL OR " +
        "UPPER(a.task.title) LIKE CONCAT('%', UPPER(:#{#filterTask.title}), '%')) " +
        "AND (:#{#filterTask.description} IS NULL OR " +
        "UPPER(a.task.description) LIKE CONCAT('%', UPPER(:#{#filterTask.description}), '%')) " +
        "AND (((:#{#filterTask.assignedEmployee.username} IS NULL) OR " +
        "(a.task.assignedEmployee.username LIKE :#{#filterTask.assignedEmployee.username})) " +
        "AND ((:#{#employeeType} IS NULL) OR " +
        "(a.task.assignedEmployee.type = :#{#employeeType}))) " +
        "AND ((:#{#filterTask.status} IS NULL) OR " +
        "(a.task.status = :#{#filterTask.status})))" +
        "ORDER BY animaltask.task.startTime")
    List<AnimalTask> findFilteredTasks(@Param("employeeType") EmployeeType employeeType, @Param("filterTask") Task filterTask);

    /**
     *Finds all Events assigned to an Animal
     * @param animalId id of the animal to find associated Events from
     * @return The events associated with the given animal
     */
    @Query("SELECT new AnimalTask(animT.id, t, animT.subject) " +
        "FROM AnimalTask animT JOIN Task t ON animT.id=t.id WHERE animT.subject.id=:animalId AND t.event = true " +
        "ORDER BY t.startTime")
    List<AnimalTask> findAllAnimalEventsBySubject_Id(@Param("animalId") long animalId);

    /**
     *Finds all animal task Events
     *
     * @return All the animal events currently in the Database
     */
    @Query("SELECT new AnimalTask(animT.id, t, animT.subject) " +
        "FROM AnimalTask animT JOIN Task t ON animT.id=t.id WHERE t.event = true ORDER BY t.startTime")
    List<AnimalTask> findAllAnimalEvents();


    /**
     *Finds filtered animal task Events
     *
     * @return All the animal events currently in the Database
     */
    @Query("SELECT animT " +
        "FROM AnimalTask animT JOIN Task t ON animT.id=t.id " +
        "WHERE t.event = true " +
        "AND (:#{#filterTask.title} IS NULL OR " +
        "UPPER(t.title) LIKE CONCAT('%', UPPER(:#{#filterTask.title}), '%')) " +
        "AND (:#{#filterTask.description} IS NULL OR " +
        "UPPER(t.description) LIKE CONCAT('%', UPPER(:#{#filterTask.description}), '%')) " +
        "ORDER BY t.startTime")
    List<AnimalTask> findFilteredEvents(@Param("filterTask") Task filterTask);


    /**
     * Finds Animal Event by id
     *
     * @param id identifies the event
     * @return event with corresponding id
     */
    @Query("SELECT new AnimalTask(animT.id, t, animT.subject) " +
            "FROM AnimalTask animT JOIN Task t ON animT.id=t.id WHERE t.event = true AND animT.id =:id")
    Optional<AnimalTask> findAnimalEventById(@Param("id")long id);


}
