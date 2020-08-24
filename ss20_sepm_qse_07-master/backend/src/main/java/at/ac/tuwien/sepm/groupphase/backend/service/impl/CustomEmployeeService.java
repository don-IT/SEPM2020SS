package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import at.ac.tuwien.sepm.groupphase.backend.types.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.lang.invoke.MethodHandles;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomEmployeeService implements EmployeeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EmployeeRepository employeeRepository;
    private final AnimalRepository animalRepository;
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final AnimalTaskRepository animalTaskRepository;
    private final EnclosureTaskRepository enclosureTaskRepository;
    private final EnclosureRepository enclosureRepository;

    @Autowired
    public CustomEmployeeService(UserService userService, EmployeeRepository employeeRepository,
                                 AnimalRepository animalRepository, TaskRepository taskRepository,
                                 AnimalTaskRepository animalTaskRepository, EnclosureTaskRepository enclosureTaskRepository,
                                 EnclosureRepository enclosureRepository) {
        this.animalTaskRepository = animalTaskRepository;
        this.employeeRepository = employeeRepository;
        this.animalRepository = animalRepository;
        this.userService = userService;
        this.taskRepository = taskRepository;
        this.enclosureTaskRepository = enclosureTaskRepository;
        this.enclosureRepository = enclosureRepository;
    }

    @Override
    public Employee createEmployee(Employee employee) {
        LOGGER.debug("Creating new employee.");
        Employee exists = employeeRepository.findEmployeeByUsername(employee.getUsername());
        employeeWorkingTimesValid(employee);
        if (exists == null) return employeeRepository.save(employee);
        throw new AlreadyExistsException("Employee with this username already exists");
    }

    public List<Employee> getAll() {
        LOGGER.debug("Getting List of all employees.");
        return employeeRepository.findAll();
    }

    //This function will be the general search List function right now only Name and Type fill be filtered
    @Override
    public List<Employee> findByNameAndType(Employee employee) {
        LOGGER.debug("Getting filtered List of employees.");
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAll().withIgnoreNullValues().withIgnoreCase()
            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
            .withMatcher("type", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<Employee> example = Example.of(Employee.builder().name(employee.getName()).type(employee.getType()).build(), customExampleMatcher);
        List<Employee> employees = employeeRepository.findAll(example);
        if (employees.isEmpty())
            throw new NotFoundException("No employee fits the given criteria");
        return employees;
    }

    @Override
    public List<Animal> findAssignedAnimals(String employeeUsername) {
        LOGGER.debug("Getting List of all animals assigned to " + employeeUsername);
        Employee employee = employeeRepository.findEmployeeByUsername(employeeUsername);

        List<Animal> animals = animalRepository.findAllByCaretakers(employee);
        if (animals.isEmpty())
            throw new NotFoundException("No Animals assigned to " + employeeUsername);
        return animals;
    }

    @Override
    public List<Enclosure> findAssignedEnclosures(String employeeUsername) {
        LOGGER.debug("Getting List of all enclosures assigned to " + employeeUsername);
        Employee employee = employeeRepository.findEmployeeByUsername(employeeUsername);

        List<Animal> animals = animalRepository.findAllByCaretakers(employee);
        List<Enclosure> enclosures = new LinkedList<>();

        if (animals.isEmpty())
            throw new NotFoundException("No Animals assigned to " + employeeUsername);
        for (Animal a : animals) {
            if (a.getEnclosure() != null) {
                enclosures.add(a.getEnclosure());
            }
        }
        return enclosures;
    }


    @Override
    public void assignAnimal(String employeeUsername, long animalId) {
        LOGGER.debug("Assigning  " + employeeUsername);
        Employee employee = employeeRepository.findEmployeeByUsername(employeeUsername);
        if (employee.getType() == EmployeeType.ANIMAL_CARE) {
            List<Animal> assignedAnimals = animalRepository.findAllByCaretakers(employee);
            for (Animal a : assignedAnimals) {
                if (a.getId() == animalId) {
                    throw new AlreadyExistsException("Animal is already assigned to this Caretaker");
                }
            }
            animalRepository.assignAnimalToCaretaker(employeeUsername, animalId);
        } else {
            throw new IncorrectTypeException("Trying to assign Animal to Employee that is not ANIMAL_CARE.");
        }
    }

    @Override
    public void removeAssignedAnimal(String employeeUsername, long AnimalId) {

        LOGGER.debug("Unassigning animal with id " + AnimalId + " from " + employeeUsername);
        employeeRepository.removeAssignedAnimal(employeeUsername, AnimalId);

    }

    @Override
    public Employee findByUsername(String username) {
        LOGGER.debug("Getting Specific employee: " + username);
        return employeeRepository.findEmployeeByUsername(username);
    }

    @Override
    public void deleteEmployeeByUsername(String username) {
        LOGGER.debug("Deleting employee with username: " + username);
        Employee employee = findByUsername(username);
        if (employee == null)
            throw new NotFoundException("No employee to delete: " + username);

        List<Task> tasks = taskRepository.findAllByAssignedEmployeeOrderByStartTime(employee);
        for (Task t : tasks) {
            t.setStatus(TaskStatus.NOT_ASSIGNED);
            t.setAssignedEmployee(null);
            taskRepository.save(t);
        }
        employeeRepository.delete(employee);
        userService.deleteUser(username);
    }

    private void employeeWorkingTimesValid(Employee employee) {
        if (employee.getWorkTimeStart().isAfter(employee.getWorkTimeEnd()) || employee.getWorkTimeStart().equals(employee.getWorkTimeEnd())) {
            throw new ValidationException("The start time should not be after the end time.");
        }
    }

    @Override
    public boolean employeeIsFreeBetweenStartingAndEndtime(Employee employee, Task task) {
        LOGGER.debug("Checking if " + employee.getUsername() + " is free");
        List<Task> tasks = taskRepository.findAllByAssignedEmployeeOrderByStartTime(employee);
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();

        LocalTime workStart = employee.getWorkTimeStart();
        LocalTime workEnd = employee.getWorkTimeEnd();

        if (!start.toLocalDate().equals(end.toLocalDate()))
            throw new NotFreeException("Employee " + employee.getUsername() + " can't be assigned to this task, " +
                "they only work from " + workStart + " - " + workEnd + ". <br>" +
                "(Task is from " + start.toLocalTime() + " - " + end.toLocalTime() + ")");

        if ((start.toLocalTime().equals(workEnd) && end.toLocalTime().equals(workStart))
            || start.toLocalTime().isAfter(workEnd)     // starts after work end
            || end.toLocalTime().isAfter(workEnd)       // ends after work
            || start.toLocalTime().isBefore(workStart)  // starts before work
            || end.toLocalTime().isBefore(workStart))   // ends before start
            throw new NotFreeException("Employee " + employee.getUsername() + " can't be assigned to this task, " +
                "they only work from " + workStart + " - " + workEnd + ". <br>" +
                "(Task is from " + start.toLocalTime() + " - " + end.toLocalTime() + ")");


        for (Task t : tasks) {
            if (!t.getId().equals(task.getId())) {
                LocalDateTime existingStart = t.getStartTime();
                LocalDateTime existingEnd = t.getEndTime();
                if (existingStart.equals(start) || existingEnd.equals(end))
                    throw new NotFreeException("Employee " + employee.getUsername()
                        + " already has work (" + beautifyDateTimeFromTillStringIfSameDay(existingStart, existingEnd)
                        + ") during this task (" + beautifyDateTimeFromTillStringIfSameDay(start, end) + ").");
                if (existingStart.isBefore(start) && existingEnd.isAfter(end))
                    throw new NotFreeException("Employee " + employee.getUsername() + " " +
                        "has work (" + beautifyDateTimeFromTillStringIfSameDay(existingStart, existingEnd)
                        + ") that overlaps this task (" + beautifyDateTimeFromTillStringIfSameDay(start, end) + ").");
                if (existingStart.isAfter(start) && existingStart.isBefore(end))
                    throw new NotFreeException("Employee " + employee.getUsername()
                        + " already has work (" + beautifyDateTimeFromTillStringIfSameDay(existingStart, existingEnd)
                        + ") that starts during this task (" + beautifyDateTimeFromTillStringIfSameDay(start, end) + ").");
                if (existingEnd.isAfter(start) && existingEnd.isBefore(end))
                    throw new NotFreeException("Employee " + employee.getUsername()
                        + " has work (" + beautifyDateTimeFromTillStringIfSameDay(existingStart, existingEnd)
                        + ") that ends during this task (" + beautifyDateTimeFromTillStringIfSameDay(start, end) + ").");
            }
        }
        return true;
    }

    String beautifyDateTimeFromTillStringIfSameDay(LocalDateTime from, LocalDateTime till) {
        LOGGER.debug("FROM: " + from.toLocalDate() + " TILL: " + till.toLocalDate() + " EQUALS " + from.toLocalDate().equals(till.toLocalDate()));
        return (from.toLocalDate().equals(till.toLocalDate()) ?
            dateTimeParser(from) + " - " + till.toLocalTime().truncatedTo(ChronoUnit.MINUTES)
            : dateTimeParser(from) + " - " + dateTimeParser(till));
    }

    String dateTimeParser(LocalDateTime dt) {
        StringBuilder sb = new StringBuilder();
        sb.append(dt.getDayOfMonth())
            .append('/')
            .append(dt.getMonthValue())
            .append('/')
            .append(dt.getYear())
            .append(' ')
            .append(dt.toLocalTime().truncatedTo(ChronoUnit.MINUTES));
        return sb.toString();
    }

    @Override
    public boolean isAssignedToAnimal(String username, Long animalID) {
        LOGGER.debug("Checking if " + username + " is assigned to animal with id " + animalID);
        Employee employee = employeeRepository.findEmployeeByUsername(username);
        for (Animal a : employee.getAssignedAnimals()) {
            if (a.getId().equals(animalID))
                return true;
        }
        return false;
    }

    @Override
    public boolean isAssignedToEnclosure(String username, Long enclosureId) {
        LOGGER.debug("Checking if " + username + " is assigned to enclosure with id " + enclosureId);
        for (Enclosure e : findAssignedEnclosures(username)) {
            if (e.getId().equals(enclosureId))
                return true;
        }
        return false;
    }

    @Override
    public List<Employee> getAllAssignedToAnimal(Animal animal) {
        LOGGER.debug("Getting all employees assigned to animal with id " + animal.getId());
        return employeeRepository.findByAssignedAnimalsContains(animal);
    }


    @Override
    public List<Employee> getAllAssignedToEnclosure(Enclosure enclosure) {
        LOGGER.debug("Getting all employees assigned to enclosure with id " + enclosure.getId());

        List<Employee> assignedEmployees = employeeRepository.getEmployeesByEnclosureID(enclosure.getId());

        return assignedEmployees;
    }


    @Override
    public List<Employee> getAllDocotrs() {
        LOGGER.debug("Getting all employees of Type Doctor");
        return employeeRepository.findAllByType(EmployeeType.DOCTOR);
    }

    @Override
    public List<Employee> getAllJanitors() {
        LOGGER.debug("Getting all employees of Type Janitor");
        return employeeRepository.findAllByType(EmployeeType.JANITOR);
    }

    @Override
    public boolean hasTaskAssignmentPermissions(String usernameEmployee, Long taskId) {
        LOGGER.debug("Checking task permissions for username {} and task with id {}", usernameEmployee, taskId);
        Optional<Employee> optionalEmployee = employeeRepository.findById(usernameEmployee);
        if (optionalEmployee.isEmpty())
            throw new NotFoundException("Username doesnt belong to an Employee");
        Employee employee = optionalEmployee.get();
        if (employee.getType() == EmployeeType.DOCTOR || employee.getType() == EmployeeType.JANITOR)
            return false;
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty())
            throw new NotFoundException("Could not find Task with given Id");
        Task task = optionalTask.get();
        Optional<AnimalTask> animalTask = animalTaskRepository.findById(task.getId());
        if (animalTask.isPresent()) {
            return isAssignedToAnimal(employee.getUsername(), animalTask.get().getSubject().getId());
        }

        Optional<EnclosureTask> enclosureTask = enclosureTaskRepository.findById(task.getId());
        if (enclosureTask.isPresent()) {
            return isAssignedToEnclosure(employee.getUsername(), enclosureTask.get().getSubject().getId());
        }
        return false;
    }

    @Override
    public boolean canBeAssignedToTask(Employee employee, Task task) {
        LOGGER.debug("Checking assignment permissions for username {} and task with id {}", employee.getUsername(), task.getId());
        if (!employeeIsFreeBetweenStartingAndEndtime(employee, task))
            return false;
        Optional<AnimalTask> animalTask = animalTaskRepository.findById(task.getId());
        if (animalTask.isPresent()) {
            if (employee.getType() == EmployeeType.DOCTOR)
                return true;
            if (employee.getType() == EmployeeType.ANIMAL_CARE)
                return isAssignedToAnimal(employee.getUsername(), animalTask.get().getSubject().getId());
            if (employee.getType() == EmployeeType.JANITOR)
                throw new IncorrectTypeException("Employees of type Janitor can not be assigned to Animal Tasks");
        }

        Optional<EnclosureTask> enclosureTask = enclosureTaskRepository.findById(task.getId());
        if (enclosureTask.isPresent()) {
            if (employee.getType() == EmployeeType.JANITOR)
                return true;
            if (employee.getType() == EmployeeType.ANIMAL_CARE)
                return isAssignedToEnclosure(employee.getUsername(), enclosureTask.get().getSubject().getId());
            if (employee.getType() == EmployeeType.DOCTOR)
                throw new IncorrectTypeException("Employees of type Janitor can not be assigned to Enclosure Tasks");
        }

        return false;
    }

    public Employee findEmployeeForAnimalTask(AnimalTask animalTask, EmployeeType employeeType) {
        LOGGER.debug("Finding employee of type {} for animal task with id {}", employeeType, animalTask.getId());
        if (employeeType == EmployeeType.JANITOR)
            throw new IncorrectTypeException("Janitors cant do animal tasks");
        List<Employee> possibleEmployees = new LinkedList<>();

        //if employeeType = null both Docotors and Animal Carteakers get added to the List
        if (employeeType != EmployeeType.DOCTOR)
            possibleEmployees.addAll(getAllAssignedToAnimal(animalTask.getSubject()));
        if (employeeType != EmployeeType.ANIMAL_CARE)
            possibleEmployees.addAll(getAllDocotrs());

        if (possibleEmployees.isEmpty())
            throw new NotFoundException("There are no employees able to do this task");

        if (animalTask.getTask().isPriority()) {
            return filterEarliestAvailableEmployee(animalTask.getTask(), possibleEmployees);
        } else {
            return filterAvailableEmployeeWithLeastWork(animalTask.getTask(), possibleEmployees);
        }
    }

    public Employee findEmployeeForEnclosureTask(EnclosureTask enclosureTask, EmployeeType employeeType) {
        LOGGER.debug("Finding employee of type {} for enclosure task with id {}", employeeType, enclosureTask.getId());
        if (employeeType == EmployeeType.DOCTOR)
            throw new IncorrectTypeException("Doctors cant do enclosure tasks");
        List<Employee> possibleEmployees = new LinkedList<>();

        //if employeeType = null both Janitors and Animal Carteakers get added to the List
        if (employeeType != EmployeeType.JANITOR)
            possibleEmployees.addAll(getAllAssignedToEnclosure(enclosureTask.getSubject()));
        if (employeeType != EmployeeType.ANIMAL_CARE)
            possibleEmployees.addAll(getAllJanitors());

        if (possibleEmployees.isEmpty())
            throw new NotFoundException("There are no employees able to do this task");

        if (enclosureTask.getTask().isPriority()) {
            return filterEarliestAvailableEmployee(enclosureTask.getTask(), possibleEmployees);
        } else {
            return filterAvailableEmployeeWithLeastWork(enclosureTask.getTask(), possibleEmployees);
        }
    }

    private Employee filterAvailableEmployeeWithLeastWork(Task task, List<Employee> employees) {
        List<Employee> employeesWithTime = new LinkedList<>();
        for (Employee e : employees) {
            if (noExceptionEmployeeIsFreeBetweenStartingAndEndtime(e, task))
                employeesWithTime.add(e);
        }
        if (employeesWithTime.isEmpty())
            throw new NotFreeException("There are no available employees for the task time");
        Employee minWorkingTimeEmployee = employeesWithTime.get(0);
        for (Employee e : employeesWithTime) {
            if (getTimeSpendThisWeekInHours(e) < getTimeSpendThisWeekInHours(minWorkingTimeEmployee))
                minWorkingTimeEmployee = e;
        }
        return minWorkingTimeEmployee;
    }

    private Employee filterEarliestAvailableEmployee(Task task, List<Employee> employees) {
        LocalDateTime soonestPossibleTime = LocalDateTime.MAX;
        Employee soonestAvailableEmployee = null;

        for (Employee e : employees) {
            LocalDateTime earliestTimeForEmployee;
            try {
                earliestTimeForEmployee = earliestStartingTimeForTaskAndEmployee(task, e);
            } catch (Exception exc) {
                continue;
            }
            if (earliestTimeForEmployee.isBefore(soonestPossibleTime)) {
                soonestPossibleTime = earliestTimeForEmployee;
                soonestAvailableEmployee = e;
            }
        }

        if (soonestAvailableEmployee == null) {
            throw new NotFreeException("No employee fulfills assignment criteria for this task");
        }

        return soonestAvailableEmployee;
    }

    public double getTimeSpendThisWeekInHours(Employee employee) {
        LOGGER.debug("Finding time spend this week for employee {}", employee.getUsername());
        LocalDateTime currentDateTime = LocalDateTime.now();
        DayOfWeek currentWeekday = LocalDateTime.now().getDayOfWeek();
        List<Task> tasksOfEmployee = taskRepository.findAllByAssignedEmployeeOrderByStartTime(employee);
        double hoursThisWeek = 0.0;

        for (Task t : tasksOfEmployee) {
            //if task starts and ends in current week
            if (t.getStartTime().isAfter(currentDateTime.minusDays(currentWeekday.getValue())) && t.getEndTime().isBefore(currentDateTime.plusDays(7 - currentWeekday.getValue()))) {
                hoursThisWeek += getTaskDurationInHours(t.getStartTime(), t.getEndTime());
            }
        }

        return hoursThisWeek;
    }

    private double getTaskDurationInHours(LocalDateTime startTime, LocalDateTime endTime) {
        return (endTime.getHour() - startTime.getHour())
            + (((double) endTime.getMinute() - (double) startTime.getMinute()) / 60.0)
            + (((double) endTime.getSecond() - (double) startTime.getSecond()) / 3600.0);
    }

    public LocalDateTime earliestStartingTimeForTaskAndEmployee(Task task, Employee employee) {
        LOGGER.debug("Finding earliest starting time for a task for employee {}", employee.getUsername());
        //ordered by startingTime
        Task[] currentTasksOfEmployee = taskRepository.findAllByAssignedEmployeeOrderByStartTime(employee).toArray(Task[]::new);
        //task that starts 5 min from now (since you have to get there first)
        LocalDateTime soonestStartTime = LocalDateTime.now().plusMinutes(5).withSecond(0).withNano(0);
        Task instantTask = Task.builder().startTime(soonestStartTime)
            .endTime(addDurationOfOneTaskToStartTime(soonestStartTime, task)).build();

        if (noExceptionEmployeeIsFreeBetweenStartingAndEndtime(employee, instantTask))
            return soonestStartTime;

        for (int i = 0; i < (currentTasksOfEmployee.length - 1); i++) {

            if (currentTasksOfEmployee[i].getEndTime().isAfter(soonestStartTime)) {

                if (employeeIsFreeForDurationFromStartTime(currentTasksOfEmployee[i].getEndTime(), task, employee)) {
                    return currentTasksOfEmployee[i].getEndTime();
                }

                if (!currentTasksOfEmployee[i].getEndTime().toLocalDate().isEqual(currentTasksOfEmployee[i + 1].getStartTime().toLocalDate())) {
                    LocalDateTime startOfDayNextDay = startOfNextWorkingDayFromTime(currentTasksOfEmployee[i].getEndTime(), employee);
                    if (employeeIsFreeForDurationFromStartTime(startOfDayNextDay, task, employee))
                        return startOfDayNextDay;
                }
            }
        }

        if (employeeIsFreeForDurationFromStartTime(currentTasksOfEmployee[currentTasksOfEmployee.length - 1].getEndTime(), task, employee))
            return currentTasksOfEmployee[currentTasksOfEmployee.length - 1].getEndTime();

        LocalDateTime startOfDayNextDay = startOfNextWorkingDayFromTime(currentTasksOfEmployee[currentTasksOfEmployee.length - 1].getEndTime(), employee);
        if (employeeIsFreeForDurationFromStartTime(startOfDayNextDay, task, employee))
            return startOfDayNextDay;

        throw new NotFreeException("Task is longer than employees working time");
    }

    private LocalDateTime addDurationOfOneTaskToStartTime(LocalDateTime startTime, Task task) {
        return startTime.plusHours(task.getEndTime().getHour() - task.getStartTime().getHour())
            .plusMinutes(task.getEndTime().getMinute() - task.getStartTime().getMinute())
            .plusSeconds(task.getEndTime().getSecond() - task.getStartTime().getSecond());
    }

    private LocalDateTime startOfNextWorkingDayFromTime(LocalDateTime time, Employee employee) {
        return time.plusDays(1)
            .withHour(employee.getWorkTimeStart().getHour())
            .withMinute(employee.getWorkTimeStart().getMinute())
            .withSecond(employee.getWorkTimeStart().getSecond());
    }

    private boolean employeeIsFreeForDurationFromStartTime(LocalDateTime taskStartTime, Task task, Employee employee) {
        LocalDateTime taskEndTime = addDurationOfOneTaskToStartTime(taskStartTime, task);
        Task tempTask = Task.builder().startTime(taskStartTime).endTime(taskEndTime).build();
        return noExceptionEmployeeIsFreeBetweenStartingAndEndtime(employee, tempTask);
    }

    private boolean noExceptionEmployeeIsFreeBetweenStartingAndEndtime(Employee employee, Task task) {
        try {
            return employeeIsFreeBetweenStartingAndEndtime(employee, task);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Employee editEmployee(Employee employeeToEdit, String oldUsername) {

        Employee exists = employeeRepository.findEmployeeByUsername(employeeToEdit.getUsername());
        if (exists != null) {
            employeeWorkingTimesValid(employeeToEdit);
            checkIfThereAreTaskBetweenGivenWorkHours(employeeToEdit);
            exists.setName(employeeToEdit.getName());
            exists.setEmail(employeeToEdit.getEmail());
            exists.setBirthday(employeeToEdit.getBirthday());
            exists.setWorkTimeStart(employeeToEdit.getWorkTimeStart());
            exists.setWorkTimeEnd(employeeToEdit.getWorkTimeEnd());
            return employeeRepository.save(exists);
        } else {
            throw new NotFoundException(" Can not find employee");
        }
    }

    @Override
    public boolean checkIfThereAreTaskBetweenGivenWorkHours(Employee employee) {

        LocalTime start = employee.getWorkTimeStart();
        LocalTime end = employee.getWorkTimeEnd();
        List<Task> tasks = taskRepository.findAllByAssignedEmployeeOrderByStartTime(employee);
        for (Task t : tasks) {

            LocalTime existingStart = t.getStartTime().toLocalTime();
            LocalTime existingEnd = t.getEndTime().toLocalTime();
            if (existingStart.isBefore(start))
                throw new NotFreeException("Employee " + employee.getUsername()
                    + " has task starting earlier than: " + start);
            if (existingEnd.isAfter(end))
                throw new NotFreeException("Employee " + employee.getUsername()
                    + " has task to finish after: " + end);
        }
        return false;
    }
}
