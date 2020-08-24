import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AnimalTask} from '../../dtos/animalTask';
import {TaskService} from '../../services/task.service';
import {AnimalService} from '../../services/animal.service';
import {EmployeeService} from '../../services/employee.service';
import {Employee} from '../../dtos/employee';
import {Animal} from '../../dtos/animal';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Enclosure} from '../../dtos/enclosure';
import {EnclosureService} from '../../services/enclosure.service';
import {EnclosureTask} from '../../dtos/enclosureTask';
import {AlertService} from '../../services/alert.service';
import {TimeUnits, Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {RepeatableTask} from '../../dtos/RepeatableTask';


@Component({
  selector: 'app-task-creation',
  templateUrl: './task-creation.component.html',
  styleUrls: ['./task-creation.component.css']
})
export class TaskCreationComponent implements OnInit {
  task: AnimalTask;
  enclosureTask: EnclosureTask;
  repeatableTask: RepeatableTask;

  componentId = 'task-creation';

  allEmployees: Employee[];
  allAnimals: Animal[];
  taskCreationForm: FormGroup;

  @Input() currentEmployee;
  @Input() animalsOfEmployee;
  @Input() enclosuresOfEmployee;
  employeesOfTaskSubject: Employee[];
  doctors: Employee[];
  janitors: Employee[];
  employeesFound = false;

  timeUnits = TimeUnits;
  timeUnitValues = [];

  isEnclosureTask = false;
  isAnimalTask = true;

  highPriority = false;
  normalPriority = true;

  selectEmployeeTypeMode = false;
  employeeTypeSelected = false;
  employeeTypeForAutoAssignment;
  autoAssignSubmission = false;
  isRepeatable = false;

  @Output() reloadTasks = new EventEmitter();
  submittedTask = false;

  // Event
  private uploadedEventPicture: string;

  private fileType: string;

  constructor(private taskService: TaskService, private animalService: AnimalService,
              private employeeService: EmployeeService, private formBuilder: FormBuilder,
              private alertService: AlertService) {
    this.timeUnitValues = Object.keys(TimeUnits);
  }

  ngOnInit(): void {
    this.getDoctors();
    this.getJanitors();
    this.taskCreationForm = this.formBuilder.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      assignedEmployeeUsername: [],
      subjectId: ['', Validators.required],
      priority: [false],
      duration: [''],
      amount: ['1'],
      separation: ['1'],
      separationAmount: ['1'],
      event: [false],
      publicInfo: [''],
      eventPicture: ['']
    });
    this.clearForm();
  }

  getAllAnimals() {
    this.animalService.getAnimals().subscribe(
      (animals) => {
        this.allAnimals = animals;
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation getAllAnimals');
      }
    );
  }

  getDoctors() {
    this.employeeService.getDoctors().subscribe(
      (doctors) => {
        this.doctors = doctors;
        DEBUG_LOG('Getting Doctors: ' + JSON.stringify(doctors));
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation getDoctors');
      }
    );
  }

  getJanitors() {
    this.employeeService.getJanitors().subscribe(
      (janitors) => {
        this.janitors = janitors;
        DEBUG_LOG('Getting Janitors: ' + JSON.stringify(janitors));
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation getJanitors');
      }
    );
  }

  getEmployeesOfAnimal() {
    this.employeesFound = false;
    this.employeeService.getEmployeesOfAnimal(this.taskCreationForm.controls.subjectId.value).subscribe(
      (employees) => {
        this.employeesOfTaskSubject = employees;
        this.employeesFound = true;
        DEBUG_LOG('Getting Employees of animal: ' + this.taskCreationForm.controls.subjectId.value);
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation getEmployeesOfAnimal');
      }
    );
  }

  getEmployeesOfEnclosure() {
    this.employeesFound = false;
    this.employeeService.getEmployeesOfEnclosure(this.taskCreationForm.controls.subjectId.value).subscribe(
      (employees) => {
        this.employeesOfTaskSubject = employees;
        this.employeesFound = true;
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation getEmployeesOfEnclosure');
      }
    );
  }

  taskWithAutoAssignSubmitted() {
    DEBUG_LOG('helloAuto0');
    this.autoAssignSubmission = true;
    this.taskSubmitted();
  }

  taskSubmitted() {
    DEBUG_LOG('hello0');
    this.submittedTask = true;
    if (this.taskCreationForm.valid) {
      if (this.isAnimalTask) {
        if (this.isRepeatable) {
          this.getRepeatableTaskFromForm();
          this.createRepeatableAnimalTask();
        } else {
          this.getAnimalTaskFromForm();
          this.createAnimalTask();
        }
      } else if (this.isEnclosureTask) {
        if (this.isRepeatable) {
          this.getRepeatableTaskFromForm();
          this.createRepeatableEnclosureTask();
        } else {
          this.getEnclosureTaskFromForm();
          this.createEnclosureTask();
        }
      }
    }
  }

  priorityTaskSubmitted() {
    DEBUG_LOG('hello0priority');
    this.taskCreationForm.controls['priority'].setValue(true);
    this.taskWithAutoAssignSubmitted();
  }

  getAnimalTaskFromForm() {
    DEBUG_LOG('hello1');
    let startTimeParsed;
    let endTimeParsed;
    if (this.highPriority) {
      startTimeParsed = this.parseDateForHighPriority(true);
      endTimeParsed = this.parseDateForHighPriority(false);
    } else {
      startTimeParsed = this.parseDate(this.taskCreationForm.controls.startTime.value);
      endTimeParsed = this.parseDate(this.taskCreationForm.controls.endTime.value);
    }

    const isEvent: boolean = this.taskCreationForm.controls.event.value;

    this.task = new AnimalTask(
      null,
      this.taskCreationForm.controls.title.value,
      this.taskCreationForm.controls.description.value,
      startTimeParsed,
      endTimeParsed,
      this.taskCreationForm.controls.assignedEmployeeUsername.value,
      null,
      this.taskCreationForm.controls.subjectId.value,
      null,
      this.taskCreationForm.controls.priority.value,
      isEvent,
      isEvent ? this.taskCreationForm.controls.publicInfo.value : null,
      isEvent ? this.uploadedEventPicture : null
    );
    if (this.autoAssignSubmission) {
      this.task.assignedEmployeeUsername = null;
    }
    if (this.task.assignedEmployeeUsername != null) {
      this.task.status = 'ASSIGNED';
    } else {
      this.task.status = 'NOT_ASSIGNED';
    }
  }

  getEnclosureTaskFromForm() {
    DEBUG_LOG('hello1enclosure');
    let startTimeParsed;
    let endTimeParsed;
    if (this.highPriority) {
      startTimeParsed = this.parseDateForHighPriority(true);
      endTimeParsed = this.parseDateForHighPriority(false);
    } else {
      startTimeParsed = this.parseDate(this.taskCreationForm.controls.startTime.value);
      endTimeParsed = this.parseDate(this.taskCreationForm.controls.endTime.value);
    }

    const isEvent: boolean = this.taskCreationForm.controls.event.value;

    this.enclosureTask = new EnclosureTask(
      null,
      this.taskCreationForm.controls.title.value,
      this.taskCreationForm.controls.description.value,
      startTimeParsed,
      endTimeParsed,
      this.taskCreationForm.controls.assignedEmployeeUsername.value,
      null,
      this.taskCreationForm.controls.subjectId.value,
      null,
      this.taskCreationForm.controls.priority.value,
      isEvent,
      isEvent ? this.taskCreationForm.controls.publicInfo.value : null,
      isEvent ? this.uploadedEventPicture : null
    );
    if (this.enclosureTask.assignedEmployeeUsername != null) {
      this.enclosureTask.status = 'ASSIGNED';
    } else {
      this.enclosureTask.status = 'NOT_ASSIGNED';
    }
  }

  getRepeatableTaskFromForm() {
    DEBUG_LOG('hello1');
    let startTimeParsed;
    let endTimeParsed;
    startTimeParsed = this.parseDate(this.taskCreationForm.controls.startTime.value);
    endTimeParsed = this.parseDate(this.taskCreationForm.controls.endTime.value);

    const isEvent: boolean = this.taskCreationForm.controls.event.value;

    this.repeatableTask = new RepeatableTask(
      null,
      this.taskCreationForm.controls.title.value,
      this.taskCreationForm.controls.description.value,
      startTimeParsed,
      endTimeParsed,
      this.taskCreationForm.controls.assignedEmployeeUsername.value,
      null,
      this.taskCreationForm.controls.subjectId.value,
      null,
      true,
      false,
      this.taskCreationForm.controls.amount.value,
      this.taskCreationForm.controls.separation.value,
      this.taskCreationForm.controls.separationAmount.value,
      isEvent,
      isEvent ? this.taskCreationForm.controls.publicInfo.value : null,
      isEvent ? this.uploadedEventPicture : null
    );
    if (this.autoAssignSubmission) {
      this.repeatableTask.assignedEmployeeUsername = null;
    }
    if (this.repeatableTask.assignedEmployeeUsername != null) {
      this.repeatableTask.status = 'ASSIGNED';
    } else {
      this.repeatableTask.status = 'NOT_ASSIGNED';
    }
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

  parseDateForHighPriority(mode: boolean) {
    const ten = function (x) {
      return x < 10 ? '0' + x : x;
    };
    const date = new Date(Date.now());
    date.setFullYear(date.getFullYear() + 1);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const monthZero = (month < 10) ? '0' : '';
    const dayZero = (day < 10) ? '0' : '';

    const dateParsed = year + '-' + monthZero + month + '-' + dayZero + day;
    if (mode === true) {
      return dateParsed + ' 00:00:00';
    } else {
      const duration = this.taskCreationForm.controls.duration.value;
      const time = ten(duration.hour) + ':' + ten(duration.minute) + ':' + ten(duration.second);
      return dateParsed + ' ' + time;
    }

  }

  clearForm() {
    this.taskCreationForm.reset();
    this.taskCreationForm.controls.amount.setValue(1);
    this.taskCreationForm.controls.separation.setValue('YEARS');
    this.taskCreationForm.controls.separationAmount.setValue(1);
    this.submittedTask = false;
  }

  onClose() {
    this.alertService.clear(this.componentId);
  }

  createEnclosureTask() {
    this.taskService.createNewTaskEnclosure(this.enclosureTask).subscribe(
      (enclosureTask: EnclosureTask) => {
        this.clearForm();
        this.reloadTasks.emit();
        this.alertService.success('Task was successfully created!',
          {componentId: this.componentId, title: 'Success!'},
          'task-creation createEnclosureTask');
        if (this.autoAssignSubmission) {
          this.assignAfterCreation(enclosureTask.id, 'ENCLOSURE_TASK', this.employeeTypeForAutoAssignment);
        }
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation createEnclosureTask');

      }
    );
  }


  createAnimalTask() {
    this.taskService.createNewTask(this.task).subscribe(
      (animalTask: AnimalTask) => {
        this.clearForm();
        this.reloadTasks.emit();
        this.alertService.success('Task was successfully created!',
          {componentId: this.componentId, title: 'Success!'},
          'task-creation createEnclosureTask');
        if (this.autoAssignSubmission) {
          this.assignAfterCreation(animalTask.id, 'ANIMAL_TASK', this.employeeTypeForAutoAssignment);
        }
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation createAnimalTask');
      }
    );
  }

  setToAnimalTask() {
    if (this.isAnimalTask) {
      // Do nothing
    } else {
      this.isAnimalTask = true;
      this.isEnclosureTask = false;
      this.clearSubject();
    }
  }

  setToEnclosureTask() {
    if (this.isEnclosureTask) {
      // Do nothing
    } else {
      this.isAnimalTask = false;
      this.isEnclosureTask = true;
      this.clearSubject();
    }
  }

  setToPriorityTask() {
    if (this.highPriority) {

    } else {
      this.isRepeatable = false;
      this.highPriority = true;
      this.normalPriority = false;
      this.taskCreationForm.get('startTime').clearValidators();
      this.taskCreationForm.get('endTime').clearValidators();
      this.taskCreationForm.controls.duration.setValidators([Validators.required]);
      this.taskCreationForm.controls.endTime.updateValueAndValidity();
      this.taskCreationForm.controls.startTime.updateValueAndValidity();
      // this.taskCreationForm.controls.durion.updateValueAndValidity();

      this.taskCreationForm.controls.event.reset(false);
      this.taskCreationForm.controls.publicInfo.reset('');
      this.taskCreationForm.controls.eventPicture.reset('');
      this.uploadedEventPicture = '';

    }
  }

  setToNonPriorityTask() {
    if (this.normalPriority) {
      this.isRepeatable = false;
    } else {
      this.isRepeatable = false;
      this.highPriority = false;
      this.normalPriority = true;
      this.taskCreationForm.controls.startTime.setValidators([Validators.required]);
      this.taskCreationForm.controls.endTime.setValidators([Validators.required]);
      this.taskCreationForm.get('duration').clearValidators();
      this.taskCreationForm.controls.endTime.updateValueAndValidity();
      this.taskCreationForm.controls.startTime.updateValueAndValidity();
      this.taskCreationForm.controls.duration.updateValueAndValidity();
    }
  }

  clearSubject() {
    this.taskCreationForm.controls.subjectId.reset('', Validators.required);
    if (this.employeesOfTaskSubject !== undefined) {
      this.employeesOfTaskSubject.length = 0;
    }
  }

  clearStartEndTimes() {
    this.taskCreationForm.controls.startTime.reset('', Validators.required);
    this.taskCreationForm.controls.endTime.reset('', Validators.required);
  }

  clearAlerts() {
    this.alertService.clear(this.componentId);
  }

  switchSelectEmployeeTypeMode() {
    this.selectEmployeeTypeMode = !this.selectEmployeeTypeMode;
    this.employeeTypeSelected = false;
    this.employeeTypeForAutoAssignment = null;
  }

  autoAssignAnimalTaskToDoctor(taskId) {
    this.taskService.autoAssignAnimalTaskToDoctor(taskId).subscribe(
      (res: any) => {
        this.alertService.success('Task successfully assigned!'
          , {componentId: this.componentId}, 'TaskCreation: autoAssignAnimalTaskToDoctor()');
      },
      error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskCreation: autoAssignAnimalTaskToDoctor()');
      }
    );
  }

  autoAssignAnimalTaskToCaretaker(taskId) {
    this.taskService.autoAssignAnimalTaskToCaretaker(taskId).subscribe(
      (res: any) => {
        this.alertService.success('Task successfully assigned!'
          , {componentId: this.componentId}, 'TaskCreation: autoAssignAnimalTaskToCaretaker()');
      }, error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskCreation: autoAssignAnimalTaskToCaretaker()');
      }
    );
  }

  autoAssignEnclosureTaskToCaretaker(taskId) {
    this.taskService.autoAssignEnclosureTaskToCaretaker(taskId).subscribe(
      (res: any) => {
        this.alertService.success('Task successfully assigned!'
          , {componentId: this.componentId}, 'TaskCreation: autoAssignEnclosureTaskTaskToCaretaker()');

      }, error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskCreation: autoAssignEnclosureTaskTaskToCaretaker()');
      }
    );
  }

  autoAssignEnclosureTaskToJanitor(taskId) {
    this.taskService.autoAssignEnclosureTaskToJanitor(taskId).subscribe(
      (res: any) => {
        this.alertService.success('Task successfully assigned!'
          , {componentId: this.componentId}, 'TaskCreation: autoAssignEnclosureTaskTaskToJanitor()');
      }, error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskCreation: autoAssignEnclosureTaskTaskToJanitor()');
      }
    );
  }

  assignAfterCreation(taskId, taskType, employeeType) {
    if (taskType === 'ANIMAL_TASK') {
      if (employeeType === 'DOCTOR') {
        this.autoAssignAnimalTaskToDoctor(taskId);
      } else if (employeeType === 'CARETAKER') {
        this.autoAssignAnimalTaskToCaretaker(taskId);
      }
    } else if (taskType === 'ENCLOSURE_TASK') {
      if (employeeType === 'JANITOR') {
        this.autoAssignEnclosureTaskToJanitor(taskId);
      } else if (employeeType === 'CARETAKER') {
        this.autoAssignEnclosureTaskToCaretaker(taskId);
      }
    }
    this.switchSelectEmployeeTypeMode();
  }

  selectDoctor() {
    this.employeeTypeForAutoAssignment = 'DOCTOR';
    this.employeeTypeSelected = true;
  }

  selectCaretaker() {
    this.employeeTypeForAutoAssignment = 'CARETAKER';
    this.employeeTypeSelected = true;
  }

  selectJanitor() {
    this.employeeTypeForAutoAssignment = 'JANITOR';
    this.employeeTypeSelected = true;
  }

  changeRepeatable() {
    this.setToNonPriorityTask();
    if (!this.isRepeatable) {
      this.isRepeatable = true;
    }
  }

  createRepeatableAnimalTask() {
    this.taskService.createNewTaskAnimalRepeatable(this.repeatableTask).subscribe(
      (animalTask: AnimalTask) => {
        this.clearForm();
        this.reloadTasks.emit();
        this.alertService.success('Task was successfully created!',
          {componentId: this.componentId, title: 'Success!'},
          'task-creation createEnclosureTask');
        if (this.autoAssignSubmission) {
          this.assignAfterCreationRepeatable(animalTask.id, 'ANIMAL_TASK', this.employeeTypeForAutoAssignment);
        }
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation createRepeatableEnclosureTask');
      }
    );
  }

  createRepeatableEnclosureTask() {
    this.taskService.createNewTaskEnclosureRepeatable(this.repeatableTask).subscribe(
      (enclosureTask: EnclosureTask) => {
        this.clearForm();
        this.reloadTasks.emit();
        this.alertService.success('Task was successfully created!',
          {componentId: this.componentId, title: 'Success!'},
          'task-creation createRepeatableEnclosureTask');
        if (this.autoAssignSubmission) {
          this.assignAfterCreationRepeatable(enclosureTask.id, 'ENCLOSURE_TASK', this.employeeTypeForAutoAssignment);
        }
      },
      error => {
        this.alertService.alertFromError(error,
          {componentId: this.componentId},
          'task-creation createRepeatableEnclosureTask');
      }
    );
  }

  assignAfterCreationRepeatable(taskId, taskType, employeeType) {
    if (taskType === 'ANIMAL_TASK') {
      if (employeeType === 'DOCTOR') {
        this.autoAssignAnimalTaskToDoctorRepeatable(taskId);
      } else if (employeeType === 'CARETAKER') {
        this.autoAssignAnimalTaskToCaretakerRepeatable(taskId);
      }
    } else if (taskType === 'ENCLOSURE_TASK') {
      if (employeeType === 'JANITOR') {
        this.autoAssignEnclosureTaskToJanitorRepeatable(taskId);
      } else if (employeeType === 'CARETAKER') {
        this.autoAssignEnclosureTaskToCaretakerRepeatable(taskId);
      }
    }
    this.switchSelectEmployeeTypeMode();
  }

  autoAssignAnimalTaskToDoctorRepeatable(taskId) {
    this.taskService.autoAssignAnimalTaskToDoctorRepeat(taskId).subscribe(
      (res: any) => {
        this.alertService.success('All Tasks successfully assigned!'
          , {componentId: this.componentId}, 'TaskCreation: autoAssignAnimalTaskToDoctorRepeatable()');
      },
      error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskCreation: autoAssignAnimalTaskToDoctorRepeatable()');
      }
    );
  }

  autoAssignAnimalTaskToCaretakerRepeatable(taskId) {
    this.taskService.autoAssignAnimalTaskToCaretakerRepeat(taskId).subscribe(
      (res: any) => {
        this.alertService.success('All Tasks successfully assigned!'
          , {componentId: this.componentId}, 'TaskCreation: autoAssignAnimalTaskToCaretakerRepeatable()');
      },
      error => {
        this.alertService.alertFromError(error, {componentId: this.componentId},
          'TaskCreation: autoAssignAnimalTaskToCaretakerRepeatable()');
      }
    );
  }

  autoAssignEnclosureTaskToCaretakerRepeatable(taskId) {
    this.taskService.autoAssignEnclosureTaskToCaretakerRepeat(taskId).subscribe(
      (res: any) => {
        this.alertService.success('All Tasks successfully assigned!'
          , {componentId: this.componentId}, 'TaskCreation: autoAssignEnclosureTaskToCaretakerRepeatable()');
      },
      error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskCreation: autoAssignEnclosureTaskToCaretakerRepeatable()');
      }
    );
  }

  autoAssignEnclosureTaskToJanitorRepeatable(taskId) {
    this.taskService.autoAssignEnclosureTaskToJanitorRepeat(taskId).subscribe(
      (res: any) => {
        this.alertService.success('All Tasks successfully assigned!'
          , {componentId: this.componentId}, 'TaskCreation: autoAssignEnclosureTaskToJanitorRepeatable()');
      },
      error => {
        this.alertService.alertFromError(error, {componentId: this.componentId}, 'TaskCreation: autoAssignEnclosureTaskToJanitorRepeatable()');
      }
    );
  }

  public OnImageFileSelected(event) {
    const files = event.target.files;
    const file = files[0];
    const maxSize = 259000000;
    const acceptedImageTypes = ['image/jpeg', 'image/png'];

    if (files && file) {
      if (file.size > maxSize) {
        this.alertService.warn('File is to large. Max size is: ' + maxSize / 1000 + ' MB.',
          {}, 'Enclosure component: OnImageFileSelected');
      } else {
        if (!acceptedImageTypes.includes(file.type)) {
          this.alertService.warn('File has to either be jpeg or png.' + maxSize / 1000 + ' MB.',
            {}, 'Enclosure component: OnImageFileSelected');
        } else {
          const reader = new FileReader();

          reader.onload = this._handleReaderLoaded.bind(this);
          this.fileType = 'data:' + file.type.toString() + ';base64,';
          reader.readAsBinaryString(file);
        }
      }
    }
  }

  // From: https://stackoverflow.com/questions/42482951/converting-an-image-to-base64-in-angular-2
  // Converts the resulting binary String of the reader to base 64
  _handleReaderLoaded(readerEvt) {
    const binaryString = readerEvt.target.result;
    this.uploadedEventPicture = this.fileType + btoa(binaryString);
    // this.taskCreationForm.controls.eventPicture.setValue(this.uploadedEventPicture);
  }
}
