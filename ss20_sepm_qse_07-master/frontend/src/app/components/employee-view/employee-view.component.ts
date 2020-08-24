import {Component, OnInit} from '@angular/core';
import {EmployeeService} from '../../services/employee.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Employee} from '../../dtos/employee';
import {AuthService} from '../../services/auth.service';
import {Location} from '@angular/common';
import {Animal} from '../../dtos/animal';
import {Task} from '../../dtos/task';
import {AnimalService} from '../../services/animal.service';
import {TaskService} from '../../services/task.service';
import {Enclosure} from '../../dtos/enclosure';
import {EnclosureService} from '../../services/enclosure.service';
import {AlertService} from '../../services/alert.service';
import {type, Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Component({
  selector: 'app-employee-view',
  templateUrl: './employee-view.component.html',
  styleUrls: ['./employee-view.component.css']
})
export class EmployeeViewComponent implements OnInit {

  public employee: Employee;
  currentUserType;

  currentUser: string;
  check: string;
  animalList: Animal[];
  selectedAnimal: Animal = null;
  assignedAnimals: Animal[];
  tasks: Task[];             // : AnimalTask[];
  // enclosureTasks: EnclosureTask[];

  enclosuresFound = false;
  enclosuresOfEmployee: Enclosure[];

  taskListMode: boolean;
  animalListMode: boolean;
  enclosureListMode: boolean;

  btnIsEdit: boolean = true;
  btnIsDelete: boolean = false;


  constructor(private employeeService: EmployeeService, private authService: AuthService, private route: ActivatedRoute,
              private _location: Location, private animalService: AnimalService, private router: Router,
              private taskService: TaskService, private enclosureService: EnclosureService,
              private alertService: AlertService) {
  }

  ngOnInit(): void {
    this.currentUser = (this.route.snapshot.paramMap.get('username'));
    if (this.isAdmin() && this.currentUser != null) {
      this.loadSpecificEmployee(this.currentUser);
    } else if (this.currentUser == null) {
      this.loadPersonalInfo();
    } else {
      this.alertService.warn('You are NOT authorised to see this users information!', {},
        'employee-view ngOnInit()');
    }
  }

  getCurrentUserType() {
    if (this.isAdmin()) {
      return 'ADMIN';
    } else {
      return this.employee.type;
    }
  }

  loadPersonalInfo() {
    this.employeeService.getPersonalInfo().subscribe(
      (employee: Employee) => {
        this.employee = employee;
        DEBUG_LOG('employee: ' + JSON.stringify(this.employee));
        this.showAssignedAnimalsEmployee();
        this.loadTasksOfEmployee();
        this.currentUserType = this.getCurrentUserType();
        this.toTaskMode();
        this.getEnclosuresOfEmployee();
      },
      error => {
        this.alertService.alertFromError(error, {}, 'loadPersonInfo');
      }
    );
  }

  loadSpecificEmployee(username: string) {
    this.employeeService.getEmployeeByUsername(username).subscribe(
      (employee: Employee) => {
        this.employee = employee;
        if (this.employee == null) {
          this.alertService.error('Employee with such username does not exist.', {},
            'employee-view loadSpecificEmployee(' + username + ')');
        } else {
          DEBUG_LOG('Loaded Employee: ' + this.employee.username);
          this.showAssignedAnimalsEmployee();
          this.loadTasksOfEmployee();
          this.toAnimalMode();
          this.getEnclosuresOfEmployee();
          if (this.isAdmin()) {
            this.getAllAnimals();
          }
        }
      },
      error => {
        this.alertService.alertFromError(error, {}, 'loadSpecificEmployee');
      }
    );
  }

  getEnclosuresOfEmployee() {
    if (this.employee !== null && this.employee !== undefined && this.employee.type === type.ANIMAL_CARE) {
      this.enclosuresFound = false;
      this.enclosureService.getEnclosuresOfEmployee(this.employee.username).subscribe(
        (enclosures) => {
          this.enclosuresOfEmployee = enclosures;
          this.enclosuresFound = true;
        },
        error => {
          this.alertService.alertFromError(error, {}, 'getEnclosureOfEmployee');
        }
      );
    }
  }

  loadTasksOfEmployee() {
    this.taskService.getTasksOfEmployee(this.employee.username).subscribe(
      (tasks) => {
        this.tasks = tasks;
      },
      error => {
        DEBUG_LOG('Error loading tasks!');
        this.alertService.alertFromError(error, {}, 'loadTasksOfEmployee->loadTaskOfEmployee');
      }
    );

  }

  backClicked() {
    this._location.back();
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Displays a Date Object as yyyy-mm-dd
   */
  displayDate(date: Date): string {
    let dateString: string;
    dateString = String(date).substring(0, 10);
    return dateString;
  }

  /**
   * Selects an employee from the table to display assigned animals
   */
  showAssignedAnimalsEmployee() {
    if (this.employee !== null && this.employee !== undefined && this.employee.type === type.ANIMAL_CARE) {
      this.employeeService.getAnimals(this.employee).subscribe(
        animals => {
          this.assignedAnimals = animals;
        },
        error => {
          DEBUG_LOG('Failed to load animals of ' + this.employee.username);
          this.alertService.alertFromError(error, {}, 'showAssignedAnimalsEmployee');
        }
      );
    }
  }

  /**
   * Get All current animals
   */
  getAllAnimals() {
    if (this.employee !== null && this.employee !== undefined && this.employee.type === type.ANIMAL_CARE) {
      this.animalService.getAnimals().subscribe(
        animals => {
          this.animalList = animals;
        },
        error => {
          DEBUG_LOG('Failed to load animals');
          this.alertService.alertFromError(error, {keepAfterRouteChange: true},
            'getAllAnimals');
        }
      );
    }
  }

  /**
   * Assigns animal to the selected employee
   */
  assignAnimal() {
    if (this.assignedAnimals !== undefined) {
      for (let i = 0; i < this.assignedAnimals.length; i++) {
        if (this.assignedAnimals[i].id === this.selectedAnimal.id) {
          this.alertService.error('This animal is already assigned to ' + this.employee.username,
            {}, 'loadPersonInfo');
          return;
        }
      }
      DEBUG_LOG('assigning ' + this.selectedAnimal + ' to ' + this.employee);
    }
    this.employeeService.assignAnimalToEmployee(this.selectedAnimal, this.employee).subscribe(
      () => {
        this.showAssignedAnimalsEmployee();
      },
      error => {
        DEBUG_LOG('Failed to assign animal');
        this.alertService.alertFromError(error, {}, 'assignAnimal');
      }
    );
  }

  deleteEmployee() {
    if (this.assignedAnimals !== undefined && this.assignedAnimals.length !== 0) {
      if (confirm('Employee still has animals assigned, are you sure you want to delete?')) {
        this.employeeService.deleteEmployee(this.employee.username).subscribe(
          () => {
            this.backClicked();
          },
          error => {
            DEBUG_LOG('Failed to delete employee');
            this.alertService.alertFromError(error, {}, 'deleteEmployee');
          }
        );
      }
    } else {
      this.employeeService.deleteEmployee(this.employee.username).subscribe(
        () => {
          this._location.back();
        },
        error => {
          DEBUG_LOG('Failed to delete employee');
          this.alertService.alertFromError(error, {}, 'deleteEmployee');
        }
      );
    }
  }

  toEnclosureMode() {
    this.animalListMode = false;
    this.taskListMode = false;
    this.enclosureListMode = true;
  }

  toTaskMode() {
    this.animalListMode = false;
    this.taskListMode = true;
    this.enclosureListMode = false;
  }

  toAnimalMode() {
    this.animalListMode = true;
    this.taskListMode = false;
    this.enclosureListMode = false;
  }

  toDeleteButton() {
    this.btnIsDelete = true;
    this.btnIsEdit = false;
  }

  editEmployee() {
    this.router.navigate(['/employee-edit-view/' + this.employee.username]);
  }

  toEditButton() {
    this.btnIsEdit = true;
    this.btnIsDelete = false;
  }

  toChangePassword() {
    this.router.navigate(['employee-password-change/' + this.employee.username]);
  }

  removeAssignedAnimal(animal: Animal) {
    if (animal != null) {
      this.employeeService.unassignAnimal(animal, this.employee).subscribe(
        () => {
          console.log('Removed animal' + animal);
          this.showAssignedAnimalsEmployee();
        },
        error => {
          console.log('Failed to remove animal');
          this.alertService.alertFromError(error, {}, 'removeAnimal');
        }
      );
    }

  }
}
