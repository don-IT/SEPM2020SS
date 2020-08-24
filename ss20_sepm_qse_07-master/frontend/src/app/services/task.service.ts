import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals, type, Utilities} from '../global/globals';
import {AnimalTask} from '../dtos/animalTask';
import {Observable} from 'rxjs';
import {EnclosureTask} from '../dtos/enclosureTask';
import {Employee} from '../dtos/employee';
import {Task} from '../dtos/task';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {RepeatableTask} from '../dtos/RepeatableTask';
import {Comment} from '../dtos/comment';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private animalTaskBaseUri: string = this.globals.backendUri + '/tasks/animal';
  private enclosureTaskBaseUri: string = this.globals.backendUri + '/tasks/enclosure';
  private taskBaseUri: string = this.globals.backendUri + '/tasks';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  createNewTask(task: AnimalTask): Observable<AnimalTask> {
    DEBUG_LOG('Creating Task: ' + JSON.stringify(task));
    return this.httpClient.post<AnimalTask>(this.animalTaskBaseUri + '/' + task.animalId, task);
  }

  createNewTaskAnimalRepeatable(task: RepeatableTask): Observable<AnimalTask> {
    DEBUG_LOG('Creating repeatable Task: ' + JSON.stringify(task));
    return this.httpClient.post<AnimalTask>(this.animalTaskBaseUri + '/repeatable/' + task.subjectId, task);
  }

  createNewTaskEnclosure(task: EnclosureTask): Observable<EnclosureTask> {
    DEBUG_LOG('Creating Task: ' + JSON.stringify(task));
    return this.httpClient.post<EnclosureTask>(this.enclosureTaskBaseUri + '/' + task.enclosureId, task);
  }

  createNewTaskEnclosureRepeatable(task: RepeatableTask): Observable<EnclosureTask> {
    DEBUG_LOG('Creating Task: ' + JSON.stringify(task));
    return this.httpClient.post<EnclosureTask>(this.enclosureTaskBaseUri + '/repeatable/' + task.subjectId, task);
  }

  assignTask(id, employee) {
    DEBUG_LOG('Assign Task: ' + id + ' to ' + JSON.stringify(employee));
    return this.httpClient.put<Employee>(this.taskBaseUri + '/' + id, employee);
  }

  getTasksOfAnimal(animalId): Observable<Task[]> {
    DEBUG_LOG('Get tasks of animal ' + animalId);
    return this.httpClient.get<Task[]>(this.animalTaskBaseUri + '/' + animalId);
  }

  deleteTask(id): Observable<any> {
    DEBUG_LOG('Delete Task: ' + id);
    return this.httpClient.delete(this.taskBaseUri + '/' + id);
  }

  deleteTaskRepeat(id: Observable<any>) {
    DEBUG_LOG('Delete Task and following: ' + id);
    return this.httpClient.delete(this.taskBaseUri + '/repeatable/' + id);
  }

  getAnimalTasksOfEmployee(username): Observable<AnimalTask[]> {
    DEBUG_LOG('Get animal tasks of employee ' + username);
    return this.httpClient.get<AnimalTask[]>(this.taskBaseUri + '/employee/animal-task/' + username);
  }

  markTaskAsDone(taskId): Observable<any> {
    DEBUG_LOG('Mark task as done ' + taskId);
    return this.httpClient.put(this.taskBaseUri + '/finished/' + taskId, {});
  }

  getEnclosureTasksOfEmployee(username): Observable<EnclosureTask[]> {
    DEBUG_LOG('Get enclosure tasks of employee ' + username);
    return this.httpClient.get<EnclosureTask[]>(this.taskBaseUri + '/employee/enclosure-task/' + username);
  }

  getTasksOfEnclosure(enclosureId): Observable<Task[]> {
    DEBUG_LOG('Get tasks of enclosure ' + enclosureId);
    return this.httpClient.get<Task[]>(this.taskBaseUri + '/enclosure/' + enclosureId);

  }

  getTasksOfEmployee(username): Observable<Task[]> {
    DEBUG_LOG('Get all tasks of employee ' + username);
    return this.httpClient.get<Task[]>(this.taskBaseUri + '/employee/' + username);
  }

  getAllEvents(): Observable<Task[]> {
    DEBUG_LOG('Get all events');
    return this.httpClient.get<Task[]>(this.taskBaseUri + '/events');
  }

  autoAssignAnimalTaskToDoctor(taskId) {
    DEBUG_LOG('Auto-assign animal task to a doctor ' + taskId);
    return this.httpClient.post(this.taskBaseUri + '/auto/animal/doctor/' + taskId, {});
  }

  autoAssignAnimalTaskToDoctorRepeat(taskId) {
    DEBUG_LOG('Auto-assign animal task and following to a doctor ' + taskId);
    return this.httpClient.post(this.taskBaseUri + '/auto/animal/doctor/repeat/' + taskId, {});
  }

  autoAssignAnimalTaskToCaretaker(taskId) {
    DEBUG_LOG('Auto-assign animal task to a caretaker ' + taskId);
    return this.httpClient.post(this.taskBaseUri + '/auto/animal/caretaker/' + taskId, {});
  }

  autoAssignAnimalTaskToCaretakerRepeat(taskId) {
    DEBUG_LOG('Auto-assign animal task and following to a caretaker ' + taskId);
    return this.httpClient.post(this.taskBaseUri + '/auto/animal/caretaker/repeat/' + taskId, {});
  }

  autoAssignEnclosureTaskToCaretaker(taskId) {
    DEBUG_LOG('Auto-assign enclosure task to a caretaker ' + taskId);
    return this.httpClient.post(this.taskBaseUri + '/auto/enclosure/caretaker/' + taskId, {});
  }

  autoAssignEnclosureTaskToCaretakerRepeat(taskId) {
    DEBUG_LOG('Auto-assign enclosure task and following to a caretaker ' + taskId);
    return this.httpClient.post(this.taskBaseUri + '/auto/enclosure/caretaker/repeat/' + taskId, {});
  }

  autoAssignEnclosureTaskToJanitor(taskId) {
    DEBUG_LOG('Auto-assign enclosure task to a janitor ' + taskId);
    return this.httpClient.post(this.taskBaseUri + '/auto/enclosure/janitor/' + taskId, {});
  }

  autoAssignEnclosureTaskToJanitorRepeat(taskId) {
    DEBUG_LOG('Auto-assign enclosure task and following to a janitor ' + taskId);
    return this.httpClient.post(this.taskBaseUri + '/auto/enclosure/janitor/repeat/' + taskId, {});
  }

  updateFullTaskInformation(task: Task): Observable<any> {
    DEBUG_LOG('Update full task information ' + JSON.stringify(task));
    return this.httpClient.put(this.taskBaseUri + '/update', task);
  }

  updateTaskInformationRepeat(task: Task): Observable<any> {
    DEBUG_LOG('Update task information and following tasks ' + JSON.stringify(task));
    return this.httpClient.put(this.taskBaseUri + '/update/repeat', task);
  }


  searchTasks(employeetype: type, task: Task): Observable<Task[]> {
    DEBUG_LOG('Getting filtered list of tasks');
    DEBUG_LOG('Getting filtered list of tasks with status: ' + task.status);
    let query = '/search?';
    if (task.assignedEmployeeUsername != null && task.assignedEmployeeUsername !== '') {
      query = query + 'username=' + task.assignedEmployeeUsername + '&';
    }
    if (task.title != null && task.title !== '') {
      query = query + 'title=' + task.title + '&';
    }
    if (task.description != null && task.description !== '') {
      query = query + 'description=' + task.description + '&';
    }
    if (task.startTime != null && task.startTime !== '') {
      query = query + 'starttime=' + task.startTime + '&';
    }
    if (task.endTime != null && task.endTime !== '') {
      query = query + 'endtime=' + task.endTime + '&';
    }
    if (task.status != null) {
      query = query + 'taskStatus=' + task.status + '&';
    }
    if (employeetype != null) {
      query = query + 'employeeType=' + employeetype + '&';
    }
    query = query.substring(0, query.length - 1);
    return this.httpClient.get<Task[]>(this.taskBaseUri + query);
  }

  searchEvents(task: Task): Observable<Task[]> {
    DEBUG_LOG('Getting filtered list of events');
    let query = '/search?';
    if (task.title != null && task.title !== '') {
      query = query + 'title=' + task.title + '&';
    }
    if (task.description != null && task.description !== '') {
      query = query + 'description=' + task.description + '&';
    }
    if (task.startTime != null && task.startTime !== '') {
      query = query + 'date=' + task.startTime + '&';
    }
    query = query.substring(0, query.length - 1);
    return this.httpClient.get<Task[]>(this.taskBaseUri + '/events' + query);
  }
}
