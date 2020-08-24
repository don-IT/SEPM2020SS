import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Employee} from '../../dtos/employee';
import {AnimalTask} from '../../dtos/animalTask';
import {TaskService} from '../../services/task.service';
import {Animal} from '../../dtos/animal';
import {EnclosureTask} from '../../dtos/enclosureTask';
import {Task} from '../../dtos/task';
import {AlertService} from '../../services/alert.service';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Component({
  selector: 'app-assign-task',
  templateUrl: './assign-task.component.html',
  styleUrls: ['./assign-task.component.css']
})
export class AssignTaskComponent implements OnInit {
  @Input() task: Task;
  @Input() employees: Employee[];
  @Input() doctors: Employee [];
  @Input() janitors: Employee[];
  @Input() index: number;
  @Output() assignmentSuccessful = new EventEmitter();
  selectEmployeeTypeMode = false;

  componentId = 'assign-task';

  enable = true;
  selectedEmployee: Employee;

  employeeType;

  // error = false;
  // errorMessage = '';
  //
  // success = false;
  // lastAssignmentSuccessful = false;

  uniqId;

  constructor(private taskService: TaskService, private alertService: AlertService) {
  }

  ngOnInit(): void {
  }


  assign() {
    if (this.selectedEmployee != null) {
      this.taskService.assignTask(this.task.id, this.selectedEmployee).subscribe(
        (res: any) => {
          this.enable = false;
          this.alertService.success('Task was successfully assigned!',
            {componentId: this.componentId, title: 'Success!'},
            'assign-task assign');
        },
        error => {
          this.alertService.alertFromError(error,
            {componentId: this.componentId},
            'assign-task assign');
        }
      );
    } else {
      this.alertService.warn('An employee needs to be selected!', {componentId: this.componentId}, 'assign');
    }
  }

  vanishAll() {
    DEBUG_LOG('VANISH ALL');
    this.selectedEmployee = null;
    this.alertService.clear(this.componentId);
  }

  autoAssignAnimalTaskToDoctor(taskId) {
    this.taskService.autoAssignAnimalTaskToDoctor(taskId).subscribe(
      (res: any) => {
        this.alertService.success('Task successfully assigned!'
          , {componentId: this.componentId}, 'Assign: autoAssignAnimalTaskToDoctor()');
      },
      error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'Assign: autoAssignAnimalTaskToDoctor()');
      }
    );
  }

  autoAssignAnimalTaskToCaretaker(taskId) {
    this.taskService.autoAssignAnimalTaskToCaretaker(taskId).subscribe(
      (res: any) => {
        this.alertService.success('Task successfully assigned!'
          , {componentId: this.componentId}, 'Assign: autoAssignAnimalTaskToCaretaker()');
      }, error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'Assign: autoAssignAnimalTaskToCaretaker()');
      }
    );
  }

  autoAssignEnclosureTaskToCaretaker(taskId) {
    this.taskService.autoAssignEnclosureTaskToCaretaker(taskId).subscribe(
      (res: any) => {
        this.alertService.success('Task successfully assigned!'
          , {componentId: this.componentId}, 'Assign: autoAssignEnclosureTaskTaskToCaretaker()');

      }, error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'Assign: autoAssignEnclosureTaskTaskToCaretaker()');
      }
    );
  }

  autoAssignEnclosureTaskToJanitor(taskId) {
    this.taskService.autoAssignEnclosureTaskToJanitor(taskId).subscribe(
      (res: any) => {
        this.alertService.success('Task successfully assigned!'
          , {componentId: this.componentId}, 'Assign: autoAssignEnclosureTaskTaskToJanitor()');
      }, error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'Assign: autoAssignEnclosureTaskTaskToJanitor()');
      }
    );
  }

  assignAutomaticly(employeeType) {
    if (this.employeeType != null) {
      if (this.task.animalTask) {
        if (employeeType === 'DOCTOR') {
          this.autoAssignAnimalTaskToDoctor(this.task.id);
        } else if (employeeType === 'CARETAKER') {
          this.autoAssignAnimalTaskToCaretaker(this.task.id);
        }
      } else if (!this.task.animalTask) {
        if (employeeType === 'JANITOR') {
          this.autoAssignEnclosureTaskToJanitor(this.task.id);
        } else if (employeeType === 'CARETAKER') {
          this.autoAssignEnclosureTaskToCaretaker(this.task.id);
        }
      }
    } else {
      this.alertService.warn('An employee type needs to be selected!', {componentId: this.componentId}, 'assign');
    }
    this.deselect();
  }

  deselect() {
    this.employeeType = null;
  }
}
