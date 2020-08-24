import {Component, Input, OnInit} from '@angular/core';
import {EnclosureTask} from '../../dtos/enclosureTask';
import {Employee} from '../../dtos/employee';
import {Animal} from '../../dtos/animal';
import {Enclosure} from '../../dtos/enclosure';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TaskService} from '../../services/task.service';
import {AnimalService} from '../../services/animal.service';
import {EmployeeService} from '../../services/employee.service';
import {EnclosureService} from '../../services/enclosure.service';
import {AnimalTask} from '../../dtos/animalTask';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

// noinspection AngularMissingOrInvalidDeclarationInModule
@Component({
  // tslint:disable-next-line:component-selector
  selector: 'app-task_enclosure',
  templateUrl: './task_enclosure.component.html',
  styleUrls: ['./task_enclosure.component.css']
})
export class TaskEnclosureComponent implements OnInit {
  task: EnclosureTask;
  error = false;
  errorMessage = '';
  allEmployees: Employee[];
  employeesOfEnclosure: Employee[];
  employeesJanitors: Employee[];
  taskCreationForm: FormGroup;
  employeesFound: boolean;

  submittedTask = false;
  private success: boolean;
  constructor(private taskService: TaskService, private enclosureService: EnclosureService,
              private employeeService: EmployeeService, private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.getEmployeesOfEnclosure();
    this.taskCreationForm = this.formBuilder.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      assignedEmployeeUsername: [],
      enclosureId: ['', Validators.required]
    });
  }

  getEmployees() {
    this.employeeService.getAllEmployees().subscribe(
      (workers) => {
        this.allEmployees = workers;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  getEmployeesOfEnclosure() {
    this.employeesFound = false;
    this.employeeService.getEmployeesOfEnclosure(this.taskCreationForm.controls.enclosureId.value).subscribe(
      (employees) => {
        this.employeesOfEnclosure = employees;
        this.employeesFound = true;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }

  taskSubmitted() {
    this.error = false;
    this.success = false;
    this.submittedTask = true;
    if (this.taskCreationForm.valid) {
      const startTimeParsed = this.parseDate(this.taskCreationForm.controls.startTime.value);
      const endTimeParsed = this.parseDate(this.taskCreationForm.controls.endTime.value);
      this.task = new EnclosureTask(
        null,
        this.taskCreationForm.controls.title.value,
        this.taskCreationForm.controls.description.value,
        startTimeParsed,
        endTimeParsed,
        this.taskCreationForm.controls.assignedEmployeeUsername.value,
        null,
        this.taskCreationForm.controls.EnclosureId.value,
        null,
        false
      );
      if (this.task.assignedEmployeeUsername != null) {
        this.task.status = 'ASSIGNED';
      } else {
        this.task.status = 'NOT_ASSIGNED';
      }
      this.createTask();
      this.clearForm();
    }
  }

  clearForm() {
    this.taskCreationForm.reset();
    this.submittedTask = false;
  }

  createTask() {
    this.taskService.createNewTaskEnclosure(this.task).subscribe(
      (res: any) => {
        this.success = true;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }



  parseDate(dateUnparsed) {
    const parsed = new Date(dateUnparsed);
    const ten = function (x) {
      return x < 10 ? '0' + x : x;
    };
    const year = parsed.getFullYear();
    const month = parsed.getMonth() + 1;
    const day = parsed.getDate();
    const monthZero = (month < 10) ? '0' : '';
    const dayZero = (day < 10) ? '0' : '';
    const date = year + '-' + monthZero + month + '-' + dayZero + day;
    const time = ten(parsed.getHours()) + ':' + ten(parsed.getMinutes()) + ':' + ten(parsed.getSeconds());

    return date + ' ' + time;
  }


  vanishError() {
    this.error = false;
  }

  vanishSuccess() {
    this.success = false;
  }

  private defaultServiceErrorHandling(error: any) {
    DEBUG_LOG(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

}
