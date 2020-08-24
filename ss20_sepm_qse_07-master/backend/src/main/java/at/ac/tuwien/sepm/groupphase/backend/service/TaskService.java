package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;

import java.time.temporal.ChronoUnit;
import java.util.List;

public interface TaskService {

    /**
     * Method to create an AnimalTask
     * Requirements for assignment:
     * The employee assigned to the task must either be a Doctor or Animal Caretaker,
     * The employee must have no other tasks assigned between start and end time
     * @param task to be created
     * @param animal animal the task is assigned to
     * */
    AnimalTask createAnimalTask(Task task, Animal animal);

    /**
     * Method to create an EnclosureTask
     * Requirements for assignment:
     * The employee assigned to the task must either be a Janitor or Animal Caretaker,
     * The employee must have no other tasks assigned between start and end time
     * @param task to be created
     * @param enclosure enclosure the task is assigned to
     * */
    EnclosureTask createEnclosureTask(Task task, Enclosure enclosure);

    /**
     * Delete all AnimalTasks belonging to an Animal
     * @param animalId you want to delete all AnimalTasks for
     * */
    void deleteAnimalTasksBelongingToAnimal(Long animalId);

    /**
     * Assign Employee to existing Task without assignment
     * @param taskId Id of the task you want to assign the employee to
     * @param assignedEmployee employee you want to assign
     * */
    void updateTask(Long taskId, Employee assignedEmployee);

    /**
     * Get all tasks belonging to one animal
     * @param animalId is the id of the animal
     * @return list of animalTasks
     */
    List<AnimalTask> getAllTasksOfAnimal(Long animalId);

    /**
     * Get all events belonging to one animal
     * @param animalId is the id of the animal
     * @return list of event animalTasks
     */
    List<AnimalTask> getAllEventsOfAnimal(Long animalId);

    /**
     * Get all animal events
     * @return list of all event animalTasks
     */
    List<AnimalTask> getAllAnimalEvents();

    /**
     * Delete a task
     * @param taskId of the Task that will be deleted
     */
    void deleteTask(Long taskId);

    /**
     * Get All AnimalTasks Of an Employee
     * @param employeeUsername is the username of the employee
     * @return list of animalTasks
     */
    List<AnimalTask> getAllAnimalTasksOfEmployee(String employeeUsername);

    /**
     * Marks task as done
     * @param taskId task id of task to mark as done
     */
    void markTaskAsDone(Long taskId);

    /**
     * Checks if an employee is the one performing the task
     * @param taskId id of task to check
     * @param employeeUsername username of employee to check
     */
    boolean isTaskPerformer(String employeeUsername, Long taskId);

    /**
     * Get All EnclosureTasks Of an Employee
     * @param employeeUsername is the username of the employee
     * @return list of enclosureTasks
     */
    List<EnclosureTask> getAllEnclosureTasksOfEmployee(String employeeUsername);

    /**
     * Get all tasks belonging to one enclosure
     * @param enclosureId is the id of the enclosure
     * @return list of enclosureTasks
     */
    List<EnclosureTask> getAllTasksOfEnclosure(Long enclosureId);

    /**
     * Get all events belonging to one enclosure
     * @param enclosureId is the id of the enclosure
     * @return list of event enclosureTasks
     */
    List<EnclosureTask> getAllEventsOfEnclosure(Long enclosureId);

    /**
     * Get all enclsure events
     * @return list of all event enclosureTasks
     */
    List<EnclosureTask> getAllEnclosureEvents();


    /**
     * Assign a currently unassigned AnimalTask to an Employee,
     * If its a priority tasks soonest possible time is found,
     * otherwise it will be assigned to the least busy worker
     * @param enclosureTaskId is the id of the enclosureTask
     * @param employeeType type of employee this task is for
     */
    void automaticallyAssignEnclosureTask(Long enclosureTaskId, EmployeeType employeeType);

    /**
     * Assign a currently unassigned AnimalTask to an Employee,
     * If its a priority tasks soonest possible time is found,
     * otherwise it will be assigned to the least busy worker
     * @param animalTaskId is the id of the animalTask
     * @param employeeType type of employee this task is for
     */
    void automaticallyAssignAnimalTask(Long animalTaskId, EmployeeType employeeType);

    Task getTaskById(Long taskId);

    Task getEventById(Long taskId);

    void updateFullAnimalTaskInformation(AnimalTask animalTask);

    void updateFullEnclosureTaskInformation(EnclosureTask enclosureTask);

    EnclosureTask getEnclosureTaskById(Long enclosureTaskId);

    AnimalTask getAnimalTaskById(Long animalTaskId);

    AnimalTask getAnimalEventById(Long animalTaskId);

    EnclosureTask getEnclosureEventById(Long animalTaskId);

    /**
     * Creates a set amount of tasks
     *
     * @param task template of the tasks that will be created, Start- and Endtime for the first task
     * @param animal the tasks will be assigned to
     * @param amount of tasks that wil be created
     * @param separation which time-frame will be between the tasks
     * @param separationCount how many of the specified time frame will be between the tasks
     * @return List of AnimalTasks that are created
     */
    List<AnimalTask> createRepeatableAnimalTask(Task task, Animal animal, int amount, ChronoUnit separation, int separationCount);

    /**
     * Creates a set amount of tasks
     *
     * @param task template of the tasks that will be created, Start- and Endtime for the first task
     * @param enclosure the tasks will be assigned to
     * @param amount of tasks that wil be created
     * @param separation which time-frame will be between the tasks
     * @param separationCount how many of the specified time frame will be between the tasks
     * @return List of AnimalTasks that are created
     */
    List<EnclosureTask> createRepeatableEnclosureTask(Task task, Enclosure enclosure, int amount, ChronoUnit separation, int separationCount);

    /**
     * Deletes Task and all future instances
     *
     * @param taskId If of the first task to be deleted
     */
    void repeatDeleteTask(Long taskId);

    /**
     * Edits priority, title, description and subject of this task and all that follow.
     *
     * @param animalTask with id of the first task to be changed, and the new desired properties
     */
    void repeatUpdateAnimalTaskInformation(AnimalTask animalTask);

    /**
     * Edits priority, title, description and subject of this task and all that follow.
     *
     * @param enclosureTask with id of the first task to be changed, and the new desired properties
     */
    void repeatUpdateEnclosureTaskInformation(EnclosureTask enclosureTask);

    /**
     * Assign a currently unassigned AnimalTask and all following Tasks to an Employee,
     * It will be assigned to the least busy worker
     * @param animalTaskId is the id of the animalTask
     * @param employeeType type of employee this task is for
     */
    void automaticallyAssignAnimalTaskRepeat(Long animalTaskId, EmployeeType employeeType);

    /**
     * Assign a currently unassigned AnimalTask and all following Tasks to an Employee,
     * It will be assigned to the least busy worker
     * @param enclosureTaskId is the id of the animalTask
     * @param employeeType type of employee this task is for
     */
    void automaticallyAssignEnclosureTaskRepeat(Long enclosureTaskId, EmployeeType employeeType);

    /**
     * backend/src/main/java/at/ac/tuwien/sepm/groupphase/backend/service/TaskService.java
     * Search for a filtered list of all current Tasks
     * Title and Description search for Substring
     * Employee Username null = every employee Otherwise exact match
     * Only tasks with start and endtime between specified start and endtime
     * are returned if starttime = null then all tasks up to endtime
     * if endtime = null then all tasks from starttime
     * @param filterTask contains the fields for filtering
     * @param employeeType type of employee searched (exact match required or null for all types)
     */
    List<AnimalTask> searchAnimalTasks(EmployeeType employeeType, Task filterTask);

    /**
     * Search for a filtered list of all current Tasks
     * Title and Description search for Substring
     * Employee Username null = every employee Otherwise exact match
     * Only tasks with start and endtime between specified start and endtime
     * are returned if starttime = null then all tasks up to endtime
     * if endtime = null then all tasks from starttime
     * @param filterTask contains the fields for filtering
     * @param employeeType type of employee searched (exact match required or null for all types)
     */
    List<EnclosureTask> searchEnclosureTasks(EmployeeType employeeType, Task filterTask);

    /**
     * Checks whether a task with the given id is an animalTask
     *
     * @param taskId of the task to be checked
     * @return true if an animalTask with the given id exists (if not it should be an enclosure task)
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException if no Task with the Id exists
     */
    boolean isAnimalTask(Long taskId);


    /**
     * Search for a filtered list of all current Enclosure Events
     * Title and Description search for Substring
     * Only tasks with start and endtime between specified start and endtime
     * are returned if starttime = null then all tasks up to endtime
     * if endtime = null then all tasks from starttime
     * @param filterTask contains the fields for filtering
     */
    List<EnclosureTask> searchEnclosureEvents(Task filterTask);

    /**
     * Search for a filtered list of all current Animal Events
     * Title and Description search for Substring
     * Only tasks with start and endtime between specified start and endtime
     * are returned if starttime = null then all tasks up to endtime
     * if endtime = null then all tasks from starttime
     * @param filterTask contains the fields for filtering
     */
    List<AnimalTask> searchAnimalEvents(Task filterTask);

}
