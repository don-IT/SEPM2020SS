import {Injectable} from '@angular/core';
import {Globals, Utilities} from '../global/globals';
import {Employee} from '../dtos/employee';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Message} from '../dtos/message';
import {Animal} from '../dtos/animal';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {AuthRequest} from '../dtos/auth-request';
import {Enclosure} from '../dtos/enclosure';
import {NewPasswordReq} from '../dtos/newPasswordReq';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private employeeBaseUri: string = this.globals.backendUri + '/employee';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getUser(username: String): Observable<AuthRequest> {
    return this.httpClient.get<AuthRequest>(this.employeeBaseUri + '/user/' + username);
  }

  /**
   * Persists Employee to the backend
   * @param employee to persist
   */
  createEmployee(employee: Employee): Observable<Employee> {
    DEBUG_LOG('Create employee with username ' + employee.username);
    return this.httpClient.post<Employee>(this.employeeBaseUri, employee);
  }

  /**
   * Get List of all current employees
   */
  getAllEmployees(): Observable<Employee[]> {
    DEBUG_LOG('Getting all employees');
    return this.httpClient.get<Employee[]>(this.employeeBaseUri);
  }

  getDoctors(): Observable<Employee[]> {
    DEBUG_LOG('Getting all doctors.');
    return this.httpClient.get<Employee[]>(this.employeeBaseUri + '/doctors');
  }

  getJanitors(): Observable<Employee[]> {
    DEBUG_LOG('Getting all janitors.');
    return this.httpClient.get<Employee[]>(this.employeeBaseUri + '/janitors');
  }

  getEmployeesOfAnimal(animalId): Observable<Employee[]> {
    DEBUG_LOG('Getting employees of animal ' + JSON.stringify(animalId));
    return this.httpClient.get<Employee[]>(this.employeeBaseUri + '/assigned/animal/' + animalId);
  }

  getEmployeesOfEnclosure(enclosureId): Observable<Employee[]> {
    DEBUG_LOG('Getting employees of enclosure ' + JSON.stringify(enclosureId));
    return this.httpClient.get<Employee[]>(this.employeeBaseUri + '/assigned/enclosure/' + enclosureId);
  }

  /**
   * Get filtered List of current employees
   * @param employee contains search parameters (right now only name in form of substring and type relevant)
   */
  searchEmployees(employee: Employee): Observable<Employee[]> {
    DEBUG_LOG('Getting filtered list of employees type: ' + employee.type + ' name: ' + employee.name);
    let query = '/search?';
    if (employee.name != null && employee.name !== '') {
      query = query + 'name=' + employee.name + '&';
    }
    if (employee.type != null) {
      query = query + 'type=' + employee.type + '&';
    }
    query = query.substring(0, query.length - 1);
    return this.httpClient.get<Employee[]>(this.employeeBaseUri + query);
  }

  /**
   * Get all assigned animals of the employee
   * @param employee whose assigned animals will be returned
   */
  getAnimals(employee: Employee): Observable<Animal[]> {
    DEBUG_LOG('Get animals assigned to employee ' + employee.username);
    return this.httpClient.get<Animal[]>(this.employeeBaseUri + '/animal/' + employee.username);
  }

  /**
   * Assigns an animal to an employee
   * @param animal to be assigned
   * @param employee the animal will be assigned to
   */
  assignAnimalToEmployee(animal: Animal, employee: Employee): Observable<any> {
    DEBUG_LOG('Assign animal ' + animal.id + ' to employee ' + employee.username);
    return this.httpClient.post(this.employeeBaseUri + '/animal/' + employee.username, animal);
  }

  unassignAnimal(animal: Animal, employee: Employee): Observable<any> {
    return this.httpClient.put<Animal>(this.employeeBaseUri + '/animal/' + employee.username, animal);
  }

  getEmployeeByUsername(username: string): Observable<Employee> {
    DEBUG_LOG('Get info about employee ' + username);
    return this.httpClient.get<Employee>(this.employeeBaseUri + '/' + username);
  }

  getPersonalInfo(): Observable<Employee> {
    DEBUG_LOG('Get personal info.');
    return this.httpClient.get<Employee>(this.employeeBaseUri + '/info');
  }

  deleteEmployee(username: string): Observable<Employee> {
    DEBUG_LOG('Delete employee ' + username);
    return this.httpClient.delete<Employee>(this.employeeBaseUri + '/' + username);
  }

  editEmployee(employeeEdited: Employee, oldUsername: string): Observable<Employee> {
    return this.httpClient.put<Employee>(this.employeeBaseUri + '/edit/' + oldUsername , employeeEdited);
  }

  savePassword(newPasswordReq: NewPasswordReq): Observable<NewPasswordReq> {
    return this.httpClient.put<NewPasswordReq>(this.employeeBaseUri + '/editPassword/', newPasswordReq);
  }
  savePasswordByAdmin(newPasswordReq: NewPasswordReq): Observable<NewPasswordReq> {
    return this.httpClient.put<NewPasswordReq>(this.employeeBaseUri + '/editPasswordByAdmin/', newPasswordReq);
  }
}
