package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.temporal.ChronoUnit;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomTaskService implements TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TaskRepository taskRepository;

    private final EmployeeService employeeService;

    private final AnimalTaskRepository animalTaskRepository;

    private final EnclosureTaskRepository enclosureTaskRepository;

    private final RepeatableTaskRepository repeatableTaskRepository;

    @Autowired
    public CustomTaskService(TaskRepository taskRepository, AnimalTaskRepository animalTaskRepository,
                             EmployeeService employeeService, EnclosureTaskRepository enclosureTaskRepository,
                             RepeatableTaskRepository repeatableTaskRepository) {
        this.taskRepository = taskRepository;
        this.animalTaskRepository = animalTaskRepository;
        this.employeeService = employeeService;
        this.enclosureTaskRepository = enclosureTaskRepository;
        this.repeatableTaskRepository = repeatableTaskRepository;
    }

    @Override
    public AnimalTask createAnimalTask(Task task, Animal animal) {
        LOGGER.debug("Creating new Animal Task");
        Employee employee = task.getAssignedEmployee();

        if(animal == null)
            throw new NotFoundException("Could not find animal with given Id");

        validateStartAndEndTime(task);

        validateEventTask(task);

        if(employee == null) {
            task.setStatus(TaskStatus.NOT_ASSIGNED);
        }else if(employee.getType() == EmployeeType.JANITOR){
            throw new IncorrectTypeException("A Janitor cant complete an animal Task");
        }else{
            if(employee.getType() == EmployeeType.ANIMAL_CARE && !employeeService.isAssignedToAnimal(employee.getUsername(), animal.getId())){
                throw new NotAuthorisedException("You cant assign an animal caretaker that is not assigned to the animal.");
            }
            task.setStatus(TaskStatus.ASSIGNED);
        }

        if(task.getStatus() == TaskStatus.ASSIGNED && !employeeService.employeeIsFreeBetweenStartingAndEndtime(employee, task)){
            throw new NotFreeException("Employee already works on a task in the given time");
        }

        Task createdTask = taskRepository.save(task);
        AnimalTask animalTask = animalTaskRepository.save(AnimalTask.builder().id(createdTask.getId()).subject(animal).build());
        animalTask.setTask(createdTask);
        animalTask.setSubject(animal);
        return animalTask;
    }


    public void automaticallyAssignAnimalTask(Long animalTaskId, EmployeeType employeeType) {
        LOGGER.debug("Automatically assigning animal task with id {} to employee of type {}", animalTaskId, employeeType);
        Optional<AnimalTask> animalTaskOptional = animalTaskRepository.findById(animalTaskId);
        if(animalTaskOptional.isEmpty())
            throw new NotFoundException("Could not find enclosure task");
        AnimalTask animalTask = animalTaskOptional.get();
        if(animalTask.getTask().getStatus() != TaskStatus.NOT_ASSIGNED)
            throw new IncorrectTypeException("Only Tasks without an assigned employee can be automatically assigned");
        Employee assignedEmployee = employeeService.findEmployeeForAnimalTask(animalTask, employeeType);

        if(animalTask.getTask().isPriority()){
            LocalDateTime foundStartTime = employeeService.earliestStartingTimeForTaskAndEmployee(animalTask.getTask(), assignedEmployee);
            LocalDateTime foundEndTime = addDurationOfOneTaskToStartTime(foundStartTime, animalTask.getTask());
            animalTask.getTask().setStartTime(foundStartTime);
            animalTask.getTask().setEndTime(foundEndTime);
        }
        animalTask.getTask().setAssignedEmployee(assignedEmployee);
        animalTask.getTask().setStatus(TaskStatus.ASSIGNED);
        taskRepository.save(animalTask.getTask());

    }

    public void automaticallyAssignEnclosureTask(Long enclosureTaskId, EmployeeType employeeType) {
        LOGGER.debug("Automatically assigning enclosure task with id {} to employee of type {}", enclosureTaskId, employeeType);
        EnclosureTask enclosureTask = enclosureTaskRepository.findEnclosureTaskById(enclosureTaskId);
        if(enclosureTask == null)
            throw new NotFoundException("Could not find enclosure task");
        if(enclosureTask.getTask().getStatus() != TaskStatus.NOT_ASSIGNED)
            throw new IncorrectTypeException("Only Tasks without an assigned employee can be automatically assigned");
        Employee assignedEmployee = employeeService.findEmployeeForEnclosureTask(enclosureTask, employeeType);

        if(enclosureTask.getTask().isPriority()){
            LocalDateTime foundStartTime = employeeService.earliestStartingTimeForTaskAndEmployee(enclosureTask.getTask(), assignedEmployee);
            LocalDateTime foundEndTime = addDurationOfOneTaskToStartTime(foundStartTime, enclosureTask.getTask());
            enclosureTask.getTask().setStartTime(foundStartTime);
            enclosureTask.getTask().setEndTime(foundEndTime);
        }
        enclosureTask.getTask().setAssignedEmployee(assignedEmployee);
        enclosureTask.getTask().setStatus(TaskStatus.ASSIGNED);
        taskRepository.save(enclosureTask.getTask());

    }

    @Override
    public void deleteAnimalTasksBelongingToAnimal(Long animalId) {
        LOGGER.debug("Deleting Animal Task of Animal with Id " + animalId);
        List<AnimalTask> assignedAnimalTasks = animalTaskRepository.findAllAnimalTasksBySubject_Id(animalId);
        animalTaskRepository.deleteAll(assignedAnimalTasks);
    }

    @Override
    public EnclosureTask createEnclosureTask(Task task, Enclosure enclosure) {
        LOGGER.debug("Creating new Enclosure Task");
        Employee employee = task.getAssignedEmployee();

        if(enclosure == null)
            throw new NotFoundException("Could not find enclosure with given Id");

        validateStartAndEndTime(task);
        validateEventTask(task);

        if(employee == null) {
            task.setStatus(TaskStatus.NOT_ASSIGNED);
        }else if(employee.getType() == EmployeeType.DOCTOR){
            throw new IncorrectTypeException("A Doctor cant complete an Enclosure Task");
        }else{
            if(employee.getType() == EmployeeType.ANIMAL_CARE
                && !employeeService.isAssignedToEnclosure(employee.getUsername(), enclosure.getId())){
                throw new NotAuthorisedException("You cant assign an animal caretaker that is not assigned to an animal in the Enclosure.");
            }
            task.setStatus(TaskStatus.ASSIGNED);
        }

        if(task.getStatus() == TaskStatus.ASSIGNED && !employeeService.employeeIsFreeBetweenStartingAndEndtime(employee, task)){
            throw new NotFreeException("The employee does not work at the given time!");
        }


        Task createdTask = taskRepository.save(task);

        enclosureTaskRepository.save(EnclosureTask.builder().id(createdTask.getId()).subject(enclosure).build());
        return enclosureTaskRepository.findEnclosureTaskById(createdTask.getId());
    }

    private void validateStartAndEndTime(Task task) throws ValidationException {
        if(task.getStartTime().isAfter(task.getEndTime()))
            throw new ValidationException("Starting time of task cant be later than end time");

    }

    @Override
    public void updateTask(Long taskId, Employee assignedEmployee) {
        LOGGER.debug("Assigning Task with id {} to employee with username {}", taskId, assignedEmployee.getUsername());
        Task foundTask = getTaskById(taskId);
        if(foundTask.getStatus() != TaskStatus.NOT_ASSIGNED){
            throw new IncorrectTypeException("Only currently unassigned Tasks can be assigned to an Employee");
        }
        Employee employee = employeeService.findByUsername(assignedEmployee.getUsername());
        if(employeeService.canBeAssignedToTask(employee, foundTask)){
            foundTask.setAssignedEmployee(employee);
            foundTask.setStatus(TaskStatus.ASSIGNED);
            taskRepository.save(foundTask);
        }else{
            throw new IncorrectTypeException("Employee does not fulfill assignment criteria");
        }
    }

    public List<AnimalTask> getAllTasksOfAnimal(Long animalId){
        LOGGER.debug("Get All Tasks belonging to Animal with id: {}", animalId);
        return animalTaskRepository.findAllAnimalTasksBySubject_Id(animalId);
    }

    @Override
    public List<AnimalTask> getAllEventsOfAnimal(Long animalId) {
        LOGGER.debug("Get All Events belonging to Animal with id: {}", animalId);
        return animalTaskRepository.findAllAnimalEventsBySubject_Id(animalId);
    }

    @Override
    public void deleteTask(Long taskId) {
        LOGGER.debug("Deleting Task with id {}", taskId);

        Optional<Task> task = taskRepository.findById(taskId);
        if(task.isEmpty()) {
            throw new NotFoundException("Could not find Task with given Id");
        }
        Optional<AnimalTask> animalTask = animalTaskRepository.findById(taskId);
        Optional<EnclosureTask> enclosureTask = enclosureTaskRepository.findById(taskId);
        Optional<RepeatableTask> repeatableTask = repeatableTaskRepository.findById(taskId);

        if(!animalTask.isEmpty() && !enclosureTask.isEmpty()) {
            throw new InvalidDatabaseStateException("Task is both Animal and Enclosure Task, this should not happen.");
        }
        repeatableTask.ifPresent(this::deleteRepeatableTask);
        if (!animalTask.isEmpty()){
            AnimalTask foundAnimalTask = animalTask.get();
            Task foundTask = task.get();
            animalTaskRepository.delete(foundAnimalTask);
            taskRepository.delete(foundTask);
        } else if (!enclosureTask.isEmpty()) {
            enclosureTaskRepository.deleteEnclosureTaskAndBaseTaskById(taskId);
        }
    }

    private void deleteRepeatableTask(RepeatableTask repeatableTask) {
        Optional<RepeatableTask> previousTask = repeatableTaskRepository.findByFollowTask(repeatableTask.getTask());
        if(previousTask.isPresent()) {
            RepeatableTask previousTask1 = previousTask.get();
            previousTask1.setFollowTask(repeatableTask.getFollowTask());
            repeatableTaskRepository.save(previousTask1);
        }
        repeatableTaskRepository.delete(repeatableTask);
    }

    @Override
    public List<AnimalTask> getAllAnimalTasksOfEmployee(String employeeUsername) {
        LOGGER.debug("Get All Animal Tasks belonging to employee with username: {}", employeeUsername);
        Employee employee = employeeService.findByUsername(employeeUsername);
        if(employee == null)
            throw new NotFoundException("Could not find Employee with given Username");
        List<Task> taskList = new LinkedList<>(taskRepository.findAllByAssignedEmployeeOrderByStartTime(employee));
        List<AnimalTask> animalTaskList = new LinkedList<>();
        for(Task t:taskList){
            Optional<AnimalTask> animalTask = animalTaskRepository.findById(t.getId());
            animalTask.ifPresent(animalTaskList::add);
        }
        return animalTaskList;
    }

    @Override
    public void markTaskAsDone(Long taskId) {
        LOGGER.debug("Marking task with id {} as done", taskId);
        Task foundTask = getTaskById(taskId);
        foundTask.setStatus(TaskStatus.DONE);
        taskRepository.save(foundTask);
    }

    @Override
    public boolean isTaskPerformer(String employeeUsername, Long taskId) {
        LOGGER.debug("Check if employee with username {} is performing task with id {}", employeeUsername, taskId);
        Task task = getTaskById(taskId);
        if(task.getAssignedEmployee()==null) return false;
        return task.getAssignedEmployee().getUsername().equals(employeeUsername);
    }

    @Override
    public Task getTaskById(Long taskId){
        LOGGER.debug("Find task with id {}", taskId);
        Optional<Task> task = taskRepository.findById(taskId);
        Task foundTask;
        if(task.isPresent()){
            foundTask = task.get();
        }else{
            throw new NotFoundException("Could not find Task with given Id");
        }
        return foundTask;
    }

    @Override
    public Task getEventById(Long taskId) {
        LOGGER.debug("Find event with id {}", taskId);
        Optional<Task> task = taskRepository.findEventById(taskId);
        Task foundTask;
        if(task.isPresent()){
            foundTask = task.get();
        }else{
            throw new NotFoundException("Could not find Event with given Id");
        }
        return foundTask;
    }

    public AnimalTask getAnimalTaskById(Long animalTaskId){
        LOGGER.debug("Find animal task with id {}", animalTaskId);
        Optional<AnimalTask> animalTask = animalTaskRepository.findById(animalTaskId);
        AnimalTask foundTask;
        if(animalTask.isPresent()){
            foundTask = animalTask.get();
        }else{
            throw new NotFoundException("Could not find Task with given Id");
        }
        return foundTask;
    }

    public EnclosureTask getEnclosureTaskById(Long animalTaskId){
        LOGGER.debug("Find enclosure task with id {}", animalTaskId);
        Optional<EnclosureTask> enclosureTask = enclosureTaskRepository.findById(animalTaskId);
        EnclosureTask foundTask;
        if(enclosureTask.isPresent()){
            foundTask = enclosureTask.get();
        }else{
            throw new NotFoundException("Could not find Task with given Id");
        }
        return foundTask;
    }

    @Override
    public AnimalTask getAnimalEventById(Long animalTaskId){
        LOGGER.debug("Find animal Event with id {}", animalTaskId);
        Optional<AnimalTask> animalTask = animalTaskRepository.findAnimalEventById(animalTaskId);
        if(animalTask.isPresent()){
            return animalTask.get();
        }else{
            throw new NotFoundException("Could not find Animal Event with given Id");
        }
    }

    @Override
    public EnclosureTask getEnclosureEventById(Long animalTaskId){
        LOGGER.debug("Find enclosure event with id {}", animalTaskId);
        Optional<EnclosureTask> enclosureTask = enclosureTaskRepository.findEnclosureEventById(animalTaskId);
        if(enclosureTask.isPresent()){
            return enclosureTask.get();
        }else{
            throw new NotFoundException("Could not find Task with given Id");
        }
    }

    @Override
    public List<EnclosureTask> getAllEnclosureTasksOfEmployee(String employeeUsername) {
        LOGGER.debug("Get All Enclosure Tasks belonging to employee with username: {}", employeeUsername);
        validateEmployeeExists(employeeUsername);
        List<EnclosureTask> enclosureTasks = enclosureTaskRepository.findEnclosureTaskByEmployeeUsername(employeeUsername);
        return enclosureTasks;
    }

    @Override
    public List<EnclosureTask> getAllTasksOfEnclosure(Long enclosureId) {
        LOGGER.debug("Get All Tasks belonging to Enclosure with id: {}", enclosureId);
        return enclosureTaskRepository.findAllEnclosureTasksBySubject_Id(enclosureId);
    }

    @Override
    public List<EnclosureTask> getAllEventsOfEnclosure(Long enclosureId) {
        LOGGER.debug("Get All Events belonging to Enclosure with id: {}", enclosureId);
        return enclosureTaskRepository.findAllEnclosureEventsBySubject_Id(enclosureId);
    }

    @Override
    public List<EnclosureTask> getAllEnclosureEvents() {
        LOGGER.debug("Get All Enclosure Events");
        return enclosureTaskRepository.findAllEnclosureEvents();
    }

    @Override
    public List<AnimalTask> getAllAnimalEvents() {
        LOGGER.debug("Get All Animal Events");
        return animalTaskRepository.findAllAnimalEvents();
    }

    @Override
    public void updateFullAnimalTaskInformation(AnimalTask animalTask) {
        LOGGER.debug("Update full information of an animal task");
        //checking if such animal-task exists
        if(animalTask==null) throw new NotFoundException("Non existing animal task.");
        AnimalTask exists = getAnimalTaskById(animalTask.getId());

        validateStartAndEndTime(animalTask.getTask());

        //checking if employee can be assigned to task
        if(animalTask.getTask().getAssignedEmployee()!=null){
           if(!employeeService.canBeAssignedToTask(animalTask.getTask().getAssignedEmployee(),animalTask.getTask())) {
               throw new NotFreeException("This employee cannot be assigned to this task");
           }
        }
        //changing the task status to the right one
        if(animalTask.getTask().getAssignedEmployee()==null){
            animalTask.getTask().setStatus(TaskStatus.NOT_ASSIGNED);
        }
        if(animalTask.getTask().getAssignedEmployee()!=null && animalTask.getTask().getStatus()==TaskStatus.NOT_ASSIGNED){
            animalTask.getTask().setStatus(TaskStatus.ASSIGNED);
        }
        Task savedTask = taskRepository.saveAndFlush(animalTask.getTask());
        animalTask.setTask(savedTask);
        animalTaskRepository.saveAndFlush(animalTask);
    }

    @Override
    public void updateFullEnclosureTaskInformation(EnclosureTask enclosureTask) {
        LOGGER.debug("Update full information of an enclosure task");
        //checking if such enclosure task exists
        if(enclosureTask==null) throw new NotFoundException("Non existing enclosure task.");
        EnclosureTask existsEnclosureTask = getEnclosureTaskById(enclosureTask.getId());

        validateStartAndEndTime(enclosureTask.getTask());
        //checking if employee can be assigned to task
        if(enclosureTask.getTask().getAssignedEmployee()!=null){
            if(!employeeService.canBeAssignedToTask(enclosureTask.getTask().getAssignedEmployee(),enclosureTask.getTask())) {
                throw new NotFreeException("This employee cannot be assigned to this task");
            }
        }
        //changing the task status to the right one
        if(enclosureTask.getTask().getAssignedEmployee()==null){
            enclosureTask.getTask().setStatus(TaskStatus.NOT_ASSIGNED);
        }
        if(enclosureTask.getTask().getAssignedEmployee()!=null && enclosureTask.getTask().getStatus()==TaskStatus.NOT_ASSIGNED){
            enclosureTask.getTask().setStatus(TaskStatus.ASSIGNED);
        }
        Task savedTask = taskRepository.saveAndFlush(enclosureTask.getTask());
        enclosureTask.setTask(savedTask);
        enclosureTaskRepository.saveAndFlush(enclosureTask);
    }

    private void validateEmployeeExists(String employeeUsername) {
        Employee employee = employeeService.findByUsername(employeeUsername);
        if(employee == null)
            throw new NotFoundException("Could not find Employee with given Username");
    }

    private LocalDateTime addDurationOfOneTaskToStartTime(LocalDateTime startTime, Task task){
        return startTime.plusHours(task.getEndTime().getHour() - task.getStartTime().getHour())
            .plusMinutes(task.getEndTime().getMinute() - task.getStartTime().getMinute())
            .plusSeconds(task.getEndTime().getSecond() - task.getStartTime().getSecond());
    }

    @Override
    public List<AnimalTask> createRepeatableAnimalTask(Task task, Animal animal, int amount, ChronoUnit separation, int separationCount) {
        if(task.isPriority()) {
            throw new IncorrectTypeException("Priority tasks can't be repeatable.");
        }

        validateStartAndEndTime(task);

        Task newTask = addTimeToTask(task, separation, separationCount);

        List<AnimalTask> animalTasks = new LinkedList<>();

        Task nextTask = null;

        if(amount > 1) {
            animalTasks = createRepeatableAnimalTask(newTask, animal, amount - 1, separation, separationCount);
            nextTask = animalTasks.get(animalTasks.size()-1).getTask();
        }

        AnimalTask thisTask = createAnimalTask(task, animal);

        repeatableTaskRepository.save(RepeatableTask.builder().id(thisTask.getId()).followTask(nextTask).build());

        animalTasks.add(thisTask);

        return animalTasks;
    }

    @Override
    public List<EnclosureTask> createRepeatableEnclosureTask(Task task, Enclosure enclosure, int amount, ChronoUnit separation, int separationCount) {
        if(task.isPriority()) {
            throw new IncorrectTypeException("Priority tasks can't be repeatable.");
        }

        validateStartAndEndTime(task);

        Task newTask = addTimeToTask(task, separation, separationCount);

        List<EnclosureTask> enclosureTasks = new LinkedList<>();

        Task nextTask = null;

        if(amount > 1) {
            enclosureTasks = createRepeatableEnclosureTask(newTask, enclosure, amount - 1, separation, separationCount);
            nextTask = enclosureTasks.get(enclosureTasks.size()-1).getTask();
        }

        EnclosureTask thisTask = createEnclosureTask(task, enclosure);

        repeatableTaskRepository.save(RepeatableTask.builder().id(thisTask.getId()).followTask(nextTask).build());

        enclosureTasks.add(thisTask);

        return enclosureTasks;
    }

    //doesn't change the original Task
    private Task addTimeToTask(Task task, ChronoUnit separation, int separationCount) {
        LocalDateTime newStartTime = task.getStartTime().plus(separationCount, separation);
        LocalDateTime newEndTime = task.getEndTime().plus(separationCount, separation);

        return Task.builder().title(task.getTitle())
            .description(task.getDescription())
            .startTime(newStartTime)
            .endTime(newEndTime)
            .assignedEmployee(task.getAssignedEmployee())
            .status(task.getStatus())
            .priority(task.isPriority())
            .event(task.isEvent())
            .publicInfo(task.getPublicInfo())
            .eventPicture(task.getEventPicture())
            .build();
    }

    @Override
    public void repeatDeleteTask(Long taskId) {
        Optional<RepeatableTask> task = repeatableTaskRepository.findById(taskId);
        if(task.isEmpty()) {
            deleteTask(taskId);
        } else {
            deleteTask(taskId);
            Task nextTask = task.get().getFollowTask();
            if(nextTask != null) {
                repeatDeleteTask(nextTask.getId());
            }
        }
    }

    @Override
    public void repeatUpdateAnimalTaskInformation(AnimalTask animalTask) {
        AnimalTask savedAnimalTask = getAnimalTaskById(animalTask.getId());
        Task savedTask = savedAnimalTask.getTask();

        Task task = animalTask.getTask();

        savedTask.setPriority(task.isPriority());
        savedTask.setTitle(task.getTitle());
        savedTask.setDescription(task.getDescription());

        savedAnimalTask.setSubject(animalTask.getSubject());

        taskRepository.save(savedTask);
        animalTaskRepository.save(savedAnimalTask);

        Optional<RepeatableTask> repeatableTask = repeatableTaskRepository.findById(animalTask.getId());

        if(repeatableTask.isPresent()) {
            if(repeatableTask.get().getFollowTask() != null) {
                animalTask.setId(repeatableTask.get().getFollowTask().getId());
                repeatUpdateAnimalTaskInformation(animalTask);
            }
        }
    }

    @Override
    public void repeatUpdateEnclosureTaskInformation(EnclosureTask enclosureTask) {
        EnclosureTask savedEnclosureTask = getEnclosureTaskById(enclosureTask.getId());
        Task savedTask = savedEnclosureTask.getTask();

        Task task = enclosureTask.getTask();

        savedTask.setPriority(task.isPriority());
        savedTask.setTitle(task.getTitle());
        savedTask.setDescription(task.getDescription());
        savedTask.setEvent(task.isEvent());
        savedTask.setPublicInfo(task.getPublicInfo());
        savedTask.setEventPicture(task.getEventPicture());

        savedEnclosureTask.setSubject(enclosureTask.getSubject());

        taskRepository.save(savedTask);
        enclosureTaskRepository.save(savedEnclosureTask);

        Optional<RepeatableTask> repeatableTask = repeatableTaskRepository.findById(enclosureTask.getId());

        if(repeatableTask.isPresent()) {
            if(repeatableTask.get().getFollowTask() != null) {
                enclosureTask.setId(repeatableTask.get().getFollowTask().getId());
                repeatUpdateEnclosureTaskInformation(enclosureTask);
            }
        }
    }

    @Override
    public void automaticallyAssignAnimalTaskRepeat(Long animalTaskId, EmployeeType employeeType) {
        Optional<RepeatableTask> repeatableTask = repeatableTaskRepository.findById(animalTaskId);
        automaticallyAssignAnimalTask(animalTaskId, employeeType);
        if(repeatableTask.isPresent() && repeatableTask.get().getFollowTask() != null) {
            automaticallyAssignAnimalTaskRepeat(repeatableTask.get().getFollowTask().getId(), employeeType);
        }
    }

    @Override
    public void automaticallyAssignEnclosureTaskRepeat(Long enclosureTaskId, EmployeeType employeeType) {
        Optional<RepeatableTask> repeatableTask = repeatableTaskRepository.findById(enclosureTaskId);
        automaticallyAssignEnclosureTask(enclosureTaskId, employeeType);
        if(repeatableTask.isPresent() && repeatableTask.get().getFollowTask() != null) {
            automaticallyAssignEnclosureTaskRepeat(repeatableTask.get().getFollowTask().getId(), employeeType);
        }
    }


    public List<AnimalTask> searchAnimalTasks(EmployeeType employeeType, Task filterTask) {
        LOGGER.debug("Getting filtered List of Animal Tasks.");
        if(filterTask.getStartTime()==null) filterTask.setStartTime(LocalDateTime.MIN);
        if(filterTask.getEndTime()==null) filterTask.setEndTime(LocalDateTime.MAX);
        validateStartAndEndTime(filterTask);
        List<AnimalTask> animalTasks = animalTaskRepository.findFilteredTasks(employeeType, filterTask);

        return animalTasks.stream()
            .filter(e -> (e.getTask().getStartTime().isAfter(filterTask.getStartTime()) &&
                e.getTask().getEndTime().isBefore(filterTask.getEndTime()))).collect(Collectors.toList());
    }

    public List<AnimalTask> searchAnimalEvents(Task filterTask) {
        LOGGER.debug("Getting filtered List of Animal Events.");
        if(filterTask.getStartTime()==null) filterTask.setStartTime(LocalDateTime.MIN);
        if(filterTask.getEndTime()==null) filterTask.setEndTime(LocalDateTime.MAX);
        validateStartAndEndTime(filterTask);
        List<AnimalTask> animalTasks = animalTaskRepository.findFilteredEvents(filterTask);

        return animalTasks.stream()
            .filter(e -> (e.getTask().getStartTime().isAfter(filterTask.getStartTime()) &&
                e.getTask().getEndTime().isBefore(filterTask.getEndTime()))).collect(Collectors.toList());
    }

    public List<EnclosureTask> searchEnclosureTasks(EmployeeType employeeType, Task filterTask) {
        LOGGER.debug("Getting filtered List of Enclosure Tasks.");
        if(filterTask.getStartTime()==null) filterTask.setStartTime(LocalDateTime.MIN);
        if(filterTask.getEndTime()==null) filterTask.setEndTime(LocalDateTime.MAX);
        validateStartAndEndTime(filterTask);
        List<EnclosureTask> enclosureTasks = enclosureTaskRepository.findFilteredTasks(employeeType, filterTask);

        return enclosureTasks.stream()
            .filter(e -> (e.getTask().getStartTime().isAfter(filterTask.getStartTime()) &&
            e.getTask().getEndTime().isBefore(filterTask.getEndTime()))).collect(Collectors.toList());
    }

    public List<EnclosureTask> searchEnclosureEvents(Task filterTask) {
        LOGGER.debug("Getting filtered List of Enclosure Events.");
        if(filterTask.getStartTime()==null) filterTask.setStartTime(LocalDateTime.MIN);
        if(filterTask.getEndTime()==null) filterTask.setEndTime(LocalDateTime.MAX);
        validateStartAndEndTime(filterTask);
        List<EnclosureTask> enclosureTasks = enclosureTaskRepository.findFilteredEvents(filterTask);

        return enclosureTasks.stream()
            .filter(e -> (e.getTask().getStartTime().isAfter(filterTask.getStartTime()) &&
                e.getTask().getEndTime().isBefore(filterTask.getEndTime()))).collect(Collectors.toList());
    }


    @Override
    public boolean isAnimalTask(Long taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if(task.isEmpty()) {
            throw new NotFoundException("No Task with the given Id exists.");
        }
        Optional<AnimalTask> animalTask = animalTaskRepository.findById(taskId);
        return animalTask.isPresent();
    }

    private void validateEventTask(Task task)
    {
        if(task.isPriority() && task.isEvent())
            throw new ValidationException("A priority Task can not be an Event.");
    }

}
