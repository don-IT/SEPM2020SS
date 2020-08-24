package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CombinedTaskDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.exception.IncorrectTypeException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.AnimalService;
import at.ac.tuwien.sepm.groupphase.backend.service.EmployeeService;
import at.ac.tuwien.sepm.groupphase.backend.service.EnclosureService;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Component
public class CombinedTaskMapper {

    @Autowired
    public AnimalService animalService;

    @Autowired
    public TaskService taskService;

    @Autowired
    public EmployeeService employeeService;

    @Autowired
    public EnclosureService enclosureService;

    public CombinedTaskDto animalTaskToCombinedTaskDto(AnimalTask animalTask){
        if(animalTask == null) return null;

        return CombinedTaskDto.builder()
            .id(animalTask.getId())
            .title(animalTask.getTask().getTitle())
            .description(animalTask.getTask().getDescription())
            .startTime(animalTask.getTask().getStartTime())
            .endTime(animalTask.getTask().getEndTime())
            .assignedEmployeeUsername(animalTask.getTask().getAssignedEmployee()==null?null:animalTask.getTask().getAssignedEmployee().getUsername())
            .status(animalTask.getTask().getStatus())
            .subjectName(animalTask.getSubject().getName())
            .subjectId(animalTask.getSubject().getId())
            .animalTask(true)
            .priority(animalTask.getTask().isPriority())
            .event(animalTask.getTask().isEvent())
            .eventPicture(pictureByteToString(animalTask.getTask().getEventPicture()))
            .publicInfo(animalTask.getTask().getPublicInfo())
            .build();
    }

    public List<CombinedTaskDto> animalTaskListToCombinedTaskDtoList(List<AnimalTask> animalTasks){
        List<CombinedTaskDto> combinedTaskDtos = new LinkedList<>();
        for(AnimalTask a: animalTasks){
            combinedTaskDtos.add(animalTaskToCombinedTaskDto(a));
        }
        return combinedTaskDtos;
    }

    public CombinedTaskDto enclosureTaskToCombinedTaskDto(EnclosureTask enclosureTask) {
        if (enclosureTask == null) return null;

        return CombinedTaskDto.builder()
            .id(enclosureTask.getId())
            .title(enclosureTask.getTask() != null ? enclosureTask.getTask().getTitle() : null)
            .description(enclosureTask.getTask().getDescription())
            .startTime(enclosureTask.getTask().getStartTime())
            .endTime(enclosureTask.getTask().getEndTime())
            .assignedEmployeeUsername(enclosureTask.getTask().getAssignedEmployee() == null ? null : enclosureTask.getTask().getAssignedEmployee().getUsername())
            .status(enclosureTask.getTask().getStatus())
            .subjectName(enclosureTask.getSubject() != null ? enclosureTask.getSubject().getName() : null)
            .subjectId(enclosureTask.getSubject() != null ? enclosureTask.getSubject().getId() : null)
            .animalTask(false)
            .priority(enclosureTask.getTask().isPriority())
            .event(enclosureTask.getTask().isEvent())
            .eventPicture(pictureByteToString(enclosureTask.getTask().getEventPicture()))
            .publicInfo(enclosureTask.getTask().getPublicInfo())
            .build();
    }

    public List<CombinedTaskDto> enclosureTaskListToCombinedTaskDtoList(List<EnclosureTask> enclosureTasks){
        List<CombinedTaskDto> combinedTaskDtos = new LinkedList<>();
        for(EnclosureTask e: enclosureTasks){
            combinedTaskDtos.add(enclosureTaskToCombinedTaskDto(e));
        }
        return combinedTaskDtos;
    }

    public List<CombinedTaskDto> sortedEnclosureTaskListAndAnimalTaskListToSortedCombinedTaskDtoList(List<EnclosureTask> enclosureTasks, List<AnimalTask> animalTasks){
        List<CombinedTaskDto> combinedTaskDtos = new LinkedList<>();

        List<CombinedTaskDto> fromEnclosureTasks = enclosureTaskListToCombinedTaskDtoList(enclosureTasks);
        List<CombinedTaskDto> fromAnimalTasks = animalTaskListToCombinedTaskDtoList(animalTasks);

        if(fromEnclosureTasks.size() == 0)
            return fromAnimalTasks;

        if(fromAnimalTasks.size() == 0)
            return fromEnclosureTasks;

        Iterator<CombinedTaskDto> combinedTaskDtoListIterator1 = fromEnclosureTasks.listIterator();
        Iterator<CombinedTaskDto> combinedTaskDtoListIterator2 = fromAnimalTasks.listIterator();

        CombinedTaskDto task1 = combinedTaskDtoListIterator1.next();
        CombinedTaskDto task2 = combinedTaskDtoListIterator2.next();

        while(task1 != null && task2 != null){
            if(task1.getStartTime().isBefore(task2.getStartTime())){
                combinedTaskDtos.add(task1);
                if(combinedTaskDtoListIterator1.hasNext()) {
                    task1 = combinedTaskDtoListIterator1.next();
                }else{
                    task1 = null;
                }
            }else{
                combinedTaskDtos.add(task2);
                if(combinedTaskDtoListIterator2.hasNext()) {
                    task2 = combinedTaskDtoListIterator2.next();
                }else{
                    task2 = null;
                }
            }
        }

        while(task1 != null){
            combinedTaskDtos.add(task1);
            if(combinedTaskDtoListIterator1.hasNext()) {
                task1 = combinedTaskDtoListIterator1.next();
            }else{
                task1 = null;
            }
        }

        while(task2 != null){
            combinedTaskDtos.add(task2);
            if(combinedTaskDtoListIterator2.hasNext()) {
                task2 = combinedTaskDtoListIterator2.next();
            }else{
                task2 = null;
            }
        }
        return combinedTaskDtos;
    }

    public AnimalTask combinedTaskDtoToAnimalTask(CombinedTaskDto combinedTaskDto){
        if(combinedTaskDto==null) return null;
        if(!combinedTaskDto.isAnimalTask()){
            throw new IncorrectTypeException("This is not an animal task!");
        }
        Animal animal = animalService.findAnimalById(combinedTaskDto.getSubjectId());
        if(animal == null){
            throw new NotFoundException("No such animal exists.");
        }
        Task task = getTaskFromCombinedTaskDto(combinedTaskDto);;

        AnimalTask animalTask = AnimalTask.builder()
            .id(combinedTaskDto.getId())
            .subject(animal)
            .task(task)
            .build();

        return animalTask;
    }

    public EnclosureTask combinedTaskDtoToEnclosureTask(CombinedTaskDto combinedTaskDto){
        if(combinedTaskDto==null) return null;
        if(combinedTaskDto.isAnimalTask()) {
            throw new IncorrectTypeException("This is not an enclosure task!");
        }

        Enclosure enclosure = enclosureService.findById(combinedTaskDto.getSubjectId());
        if(enclosure == null){
            throw new NotFoundException("No such enclosure exists.");
        }
        Task task = getTaskFromCombinedTaskDto(combinedTaskDto);

        EnclosureTask enclosureTask = EnclosureTask.builder()
            .task(task)
            .id(combinedTaskDto.getId())
            .subject(enclosure)
            .build();

        return enclosureTask;
    }

    private Task getTaskFromCombinedTaskDto(CombinedTaskDto combinedTaskDto) {
        Employee employee = employeeService.findByUsername(combinedTaskDto.getAssignedEmployeeUsername());

        return Task.builder()
            .id(combinedTaskDto.getId())
            .assignedEmployee(employee)
            .description(combinedTaskDto.getDescription())
            .startTime(combinedTaskDto.getStartTime())
            .endTime(combinedTaskDto.getEndTime())
            .priority(combinedTaskDto.isPriority())
            .status(combinedTaskDto.getStatus())
            .title(combinedTaskDto.getTitle())
            .event(combinedTaskDto.isEvent())
            .eventPicture(pictureStringToByte(combinedTaskDto.getEventPicture()))
            .publicInfo(combinedTaskDto.getPublicInfo())
            .build();
    }

    byte[] pictureStringToByte(String pictueString) {
        return pictueString==null?null:pictueString.getBytes();
    }

    String pictureByteToString(byte[] pictueByte) {
        return pictueByte==null?null:new String(pictueByte);
    }
}
