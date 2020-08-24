import {Component, OnInit} from '@angular/core';
import {AnimalService} from '../../services/animal.service';
import {EmployeeService} from '../../services/employee.service';
import {TaskService} from '../../services/task.service';
import {Animal} from '../../dtos/animal';
import {ActivatedRoute, Router} from '@angular/router';
import {Employee} from '../../dtos/employee';
import {AuthService} from '../../services/auth.service';
import {EnclosureService} from '../../services/enclosure.service';
import {Enclosure} from '../../dtos/enclosure';
import {Task} from '../../dtos/task';
import {AlertService} from '../../services/alert.service';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Component({
  selector: 'app-animal-view',
  templateUrl: './animal-view.component.html',
  styleUrls: ['./animal-view.component.css']
})
export class AnimalViewComponent implements OnInit {
  currentAnimal: Animal;

  doctors: Employee[];
  employees: Employee[];
  tasks: Task[];
  selectedEnclosure: Enclosure;
  enclosureList: Enclosure[];
  selectedEmployee: Employee;
  employeeList: Employee[];

  constructor(private animalService: AnimalService, private employeeService: EmployeeService,
              private taskService: TaskService, private route: ActivatedRoute, private authService: AuthService,
              private enclosureService: EnclosureService, private alertService: AlertService) {
  }

  ngOnInit(): void {
    const currentAnimalId = (this.route.snapshot.paramMap.get('animalId'));
    this.getCurrentAnimal(currentAnimalId);
    if (this.isAdmin()) {
      this.getAllEnclosures();
      this.getAllEmployees();
    }
  }

  getCurrentAnimal(id) {
    this.animalService.getAnimalById(id).subscribe(
      (a: Animal) => {
        this.currentAnimal = a;
        this.getEmployeesOfAnimal();
        this.getTasksOfAnimal();
        this.getDoctors();
      },
      error => {
        this.alertService.alertFromError(error, {},
          'animal-view getCurrentAnimal(' + id + ')');
      }
    );
  }

  getTasksOfAnimal() {
    this.taskService.getTasksOfAnimal(this.currentAnimal.id).subscribe(
      (tasks: Task[]) => {
        this.tasks = tasks;
      },
      error => {
        this.alertService.alertFromError(error, {},
          'animal-view-' + this.currentAnimal.id + '  getTasksOfAnimal()');
      }
    );
  }

  getDoctors() {
    this.employeeService.getDoctors().subscribe(
      (doctors) => {
        this.doctors = doctors;
      },
      error => {
        this.alertService.alertFromError(error, {},
          'animal-view-' + this.currentAnimal.id + '  getDoctors()');
      }
    );
  }

  getEmployeesOfAnimal() {
    this.employeeService.getEmployeesOfAnimal(this.currentAnimal.id).subscribe(
      (employees) => {
        this.employees = employees;
      },
      error => {
        this.alertService.alertFromError(error, {},
          'animal-view-' + this.currentAnimal.id + '  getEmployeesOfAnimal()');
      }
    );
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  assignAnimalToEnclosureOrEmployee() {
    if (this.selectedEnclosure != null) {

      this.enclosureService.assignAnimalToEnclosure(this.currentAnimal, this.selectedEnclosure).subscribe(
        () => {
          this.selectedEnclosure = null;
        },
        error => {
          DEBUG_LOG('Failed to assign enclosure');
          this.alertService.alertFromError(error, {},
            'animal-view-' + this.currentAnimal.id + '  assignAnimalToEnclosureOrEmployee()');
        }
      );
    }
    if (this.selectedEmployee != null) {
      this.employeeService.assignAnimalToEmployee(this.currentAnimal, this.selectedEmployee).subscribe(
        () => {
          this.selectedEmployee = null;
        },
        error => {
          DEBUG_LOG('Failed to assign employee');
          this.alertService.alertFromError(error, {},
            'animal-view-' + this.currentAnimal.id + '  assignAnimalToEnclosureOrEmployee()');
        }
      );
    }
  }

  private getAllEnclosures() {
    this.enclosureService.getAllEnclosures().subscribe(
      enclosures => {
        this.enclosureList = enclosures;
      },
      error => {
        if (error.status === 404) {
          this.enclosureList.length = 0;
        }
        DEBUG_LOG('Failed to load all enclosures');
        this.alertService.alertFromError(error, {},
          'animal-view-' + this.currentAnimal.id + '  getAllEnclosures()');
      }
    );
  }

  private getAllEmployees() {
    this.employeeService.getAllEmployees().subscribe(
      employees => {
        this.employeeList = employees;
      },
      error => {
        if (error.status === 404) {
          this.employeeList.length = 0;
        }
        DEBUG_LOG('Failed to load all employees');
        this.alertService.alertFromError(error, {},
          'animal-view-' + this.currentAnimal.id + ' getAllEmployees()');
      }
    );
  }
}
