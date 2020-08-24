import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Enclosure} from '../../dtos/enclosure';
import {EnclosureService} from '../../services/enclosure.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '@angular/common';
import {Animal} from '../../dtos/animal';
import {AnimalService} from '../../services/animal.service';
import {TaskService} from '../../services/task.service';
import {Employee} from '../../dtos/employee';
import {EmployeeService} from '../../services/employee.service';
import {Task} from '../../dtos/task';
import {AlertService} from '../../services/alert.service';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-enclosure-view',
  templateUrl: './enclosure-view.component.html',
  styleUrls: ['./enclosure-view.component.css']
})
export class EnclosureViewComponent implements OnInit {
  enclosureToView: Enclosure;
  selectedAnimal: Animal = null;
  assignedAnimals: Animal[];
  alreadyAssignedEnclosureOfSelectedAnimal: Enclosure;
  animalList: Animal[];
  tasks: Task[];
  employeesAssigned: Employee[];
  janitors: Employee[];
  editing: boolean;
  animalMode: boolean = true;
  taskMode: boolean = false;

  btnIsEdit: boolean = true;
  btnIsDelete: boolean = false;


  constructor(private enclosureService: EnclosureService, private authService: AuthService,
              private route: ActivatedRoute, private router: Router, private _location: Location,
              private animalService: AnimalService, private taskService: TaskService,
              private employeeService: EmployeeService, private alertService: AlertService, private formBuilder: FormBuilder) {

  }

  ngOnInit(): void {
    const enclsureToViewId = Number(this.route.snapshot.paramMap.get('enclosureId'));
    if (this.isAdmin()) {
      this.loadAnimals();
    }
    this.loadEnclosureToView(enclsureToViewId);
    this.editing = false;
    if (this.isUser()) {
      this.toTaskMode();
    }
  }

  loadAnimals() {
    this.animalService.getAnimals().subscribe(
      animals => {
        this.animalList = animals;
      },
      error => {
        if (error.status === 404) {
          if (this.animalList !== undefined) {
            this.animalList.length = 0;
          }
        }
        DEBUG_LOG('Failed to load all animals');
        this.alertService.alertFromError(error, {}, 'EnclosureView component: loadAnimals()');
      }
    );
  }

  loadEnclosureTasks() {
    this.taskService.getTasksOfEnclosure(this.enclosureToView.id).subscribe(
      (tasks) => {
        this.tasks = tasks;
      },
      error => {
        this.alertService.alertFromError(error, {}, 'EnclosureView component: loadEnclosureTasks()');
      }
    );
  }

  loadEnclosureToView(enclosureId: number) {
    this.enclosureService.getById(enclosureId).subscribe(
      (enclosure: Enclosure) => {
        this.enclosureToView = enclosure;
        DEBUG_LOG('Loaded enclosure id: ' + enclosure.id);
        if (this.enclosureToView == null) {
          this.alertService.error('Enclosure with such id does not exist.', {}, 'loadEnclosureToView()');
        }
        if (this.isAdmin()) {
          this.showAssignedAnimalsEnclosure();
        }
        this.loadEnclosureTasks();
        this.loadEmployees();
      },
      error => {
        this.alertService.alertFromError(error, {}, 'EnclosureView component: loadEnclosureToView()');
      }
    );

  }

  loadEmployees() {
    this.employeeService.getJanitors().subscribe(
      janitors => {
        this.janitors = janitors;
      },
      error => {
        this.alertService.alertFromError(error, {}, 'EnclosureView component: loadEmployees()');
      }
    );
    this.employeeService.getEmployeesOfEnclosure(this.enclosureToView.id).subscribe(
      employees => {
        this.employeesAssigned = employees;
      },
      error => {
        this.alertService.alertFromError(error, {}, 'EnclosureView component: loadEmployees()');
      }
    );
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isUser(): boolean {
    return this.authService.getUserRole() === 'USER';
  }


  backClicked() {
    this._location.back();
  }

  /**
   * Selects an employee from the table to display assigned animals
   */
  showAssignedAnimalsEnclosure() {
    if (this.enclosureToView !== null) {
      this.enclosureService.getAssignedAnimals(this.enclosureToView).subscribe(
        animals => {
          this.assignedAnimals = animals;
        },
        error => {
          DEBUG_LOG('Failed to load animals of ' + this.enclosureToView.id);
          this.alertService.alertFromError(error, {}, 'showAssignedAnimalsEnclosure()');
        }
      );
    }
  }

  assignAnimaltoEnclosure() {
    if (this.assignedAnimals !== undefined) {
      for (let i = 0; i < this.assignedAnimals.length; i++) {
        if (this.assignedAnimals[i].id === this.selectedAnimal.id) {
          this.alertService.warn('This animal is already assigned to ' + this.enclosureToView.id);
          return;
        }
      }
    }
    this.enclosureService.getAlreadyAssignedEnclosureToAnimal(this.selectedAnimal).subscribe(
      (enclosure) => {
        this.alreadyAssignedEnclosureOfSelectedAnimal = enclosure;
        if (this.alreadyAssignedEnclosureOfSelectedAnimal !== null) {
          if (confirm('Animal is already assigned to enclosure with id: ' + this.alreadyAssignedEnclosureOfSelectedAnimal.id + '. Do you want to move ' + this.selectedAnimal.name + ' into this enclosure')) {
            DEBUG_LOG('assigning ' + this.selectedAnimal + ' to ' + this.enclosureToView);
            this.enclosureService.assignAnimalToEnclosure(this.selectedAnimal, this.enclosureToView).subscribe(
              () => {
                this.showAssignedAnimalsEnclosure();
              },
              error => {
                DEBUG_LOG('Failed to assign animal');
                this.alertService.alertFromError(error, {}, 'EnclosureView component: assignAnimalToEnclosure()');
              }
            );
          }
        } else {
          DEBUG_LOG('assigning ' + this.selectedAnimal + ' to ' + this.enclosureToView);
          this.enclosureService.assignAnimalToEnclosure(this.selectedAnimal, this.enclosureToView).subscribe(
            () => {
              this.showAssignedAnimalsEnclosure();
            },
            error => {
              DEBUG_LOG('Failed to assign animal');
              this.alertService.alertFromError(error, {}, 'EnclosureView component: assignAnimalToEnclosure()');
            }
          );
        }
      },
      error => {
        DEBUG_LOG('Failed to get enclosure where animal assigned');
        this.alertService.alertFromError(error, {}, 'EnclosureView component: assignAnimalToEnclosure()');
      }
    );

  }

  deleteEnclosure() {
    this.enclosureService.deleteEnclosure(this.enclosureToView).subscribe(
      () => {
        DEBUG_LOG('Deleted enclosure:' + this.enclosureToView.id);
        this.backClicked();
      },
      error => {
        DEBUG_LOG('Failed to delete enclosure');
        this.alertService.alertFromError(error, {}, 'EnclosureView component: deleteEnclosure()');
      }
    );
  }

  unassignAnimal(animal: Animal) {
    if (animal != null) {
      this.enclosureService.unassignAnimal(animal).subscribe(
        () => {
          DEBUG_LOG('Removed animal' + animal);
          this.showAssignedAnimalsEnclosure();
        },
        error => {
          DEBUG_LOG('Failed to remove animal');
          this.alertService.alertFromError(error, {}, 'EnclosureView component: unassignAnimal()');
        }
      );
    }

  }

  toTaskMode() {
    this.animalMode = false;
    this.taskMode = true;
  }

  toAnimalMode() {
    this.animalMode = true;
    this.taskMode = false;
  }

  toDeleteButton() {
    this.btnIsDelete = true;
    this.btnIsEdit = false;
  }

  toEditButton() {
    this.btnIsEdit = true;
    this.btnIsDelete = false;
  }

  editEnclosure() {
    this.router.navigate(['/enclosure-edit-view/' + this.enclosureToView.id]);
  }
}
