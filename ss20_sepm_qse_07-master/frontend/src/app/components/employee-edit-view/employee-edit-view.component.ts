import { Component, OnInit } from '@angular/core';
import {EmployeeService} from '../../services/employee.service';
import {AuthService} from '../../services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {AnimalService} from '../../services/animal.service';
import {TaskService} from '../../services/task.service';
import {EnclosureService} from '../../services/enclosure.service';
import {AlertService} from '../../services/alert.service';
import {Employee} from '../../dtos/employee';
import {type, Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Location} from '@angular/common';
import {NewPasswordReq} from '../../dtos/newPasswordReq';


@Component({
  selector: 'app-employee-edit-view',
  templateUrl: './employee-edit-view.component.html',
  styleUrls: ['./employee-edit-view.component.css'],
})
export class EmployeeEditViewComponent implements OnInit {

  public employee: Employee;
  currentUser: string;
  employeeEditInfo: FormGroup;
  submittedEmployee: boolean = false;
  date: string;
  time: string;
  dateConcerted: Date;
  types = type;

  typeValues = [];
  employeeLoaded: boolean;
  changingPassword: boolean;
  passwordEdit: FormGroup;
  newPasswordReq: NewPasswordReq;
  constructor(private employeeService: EmployeeService, private authService: AuthService, private route: ActivatedRoute,
              private _location: Location, private animalService: AnimalService, private router: Router,
              private taskService: TaskService, private enclosureService: EnclosureService, private formBuilder: FormBuilder,
              private alertService: AlertService) {

    this.typeValues = Object.keys(type);
    this.employeeEditInfo = this.formBuilder.group({
      username: ['', [Validators.required] ],
      email: ['', Validators.email],
      name: ['', [Validators.required]],
      birthday: ['', [Validators.required]],
      startTime: [''],
      workStartTime: [''],
      workEndTime: ['']
    });
    this.passwordEdit = this.formBuilder.group({
      password: ['', Validators.compose([
        Validators.required,
        Validators.minLength(8),
        Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]+$')
      ])],
      confirmPassword: ['']
    });
    this.employeeLoaded = false;
  }
  editingOn() {
    this.date = this.displayDate(this.employee.birthday);
    this.employeeEditInfo = this.formBuilder.group({
      username: [this.employee.username, [Validators.required] ],
      email: [this.employee.email, Validators.email],
      name: [this.employee.name, [Validators.required] ],
      employeeType: [this.employee.type, [Validators.required] ],
      birthday: [this.date, [Validators.required]],
      workStartTime: [this.employee.workTimeStart],
      workEndTime: [this.employee.workTimeEnd]
    });
    this.employeeLoaded = true;
  }


  /**
   * Displays a Date Object as yyyy-mm-dd
   */
  displayDate(dateUnparsed: Date): string {
    const parsed = new Date(dateUnparsed);
    const year = parsed.getFullYear();
    const month = parsed.getMonth() + 1;
    const day = parsed.getDate();
    const monthZero = (month < 10) ? '0' : '';
    const dayZero = (day < 10) ? '0' : '';
    this.date = year + '-' + monthZero + month + '-' + dayZero + day;
    return this.date;
  }

  ngOnInit(): void {
    this.currentUser = (this.route.snapshot.paramMap.get('username'));
    if (this.currentUser != null) {
      this.loadSpecificEmployee(this.currentUser);
      this.changingPassword = false;
    }
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
        }
        this.editingOn();
      },
      error => {
        this.alertService.alertFromError(error,  {}, 'loadSpecificEmployee');
      }
    );
  }

  cancelChanges() {
    if (this.changingPassword === true) {
      this.changingPassword = false;
    } else {
      this.backClicked();
    }

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

  saveChanges() {
    this.submittedEmployee = true;
    this.dateConcerted = new Date(this.date);
    if (this.employeeEditInfo.controls.name.value === '') {
      this.alertService.info('Name cannot be empty');
    } else if (this.employeeEditInfo.controls.birthday.value === '') {
      this.alertService.info('User Birthday cannot be empty');
    } else {
      const employeeEdited: Employee = new Employee(
        this.employee.username,
        this.employeeEditInfo.controls.email.value,
        'Justforcheck7',
        this.employeeEditInfo.controls.name.value,
        this.employeeEditInfo.controls.birthday.value,
        this.employeeEditInfo.controls.workStartTime.value,
        this.employeeEditInfo.controls.workEndTime.value,
        this.employee.type
        );

      this.employeeService.editEmployee(employeeEdited, this.employee.username).subscribe(
        () => {
          DEBUG_LOG('edited employee' + this.employee.username);
           this.backClicked();
        },
        error => {
          this.editingOn();
          DEBUG_LOG('Failed to edit enclosure');
          this.alertService.alertFromError(error, {}, 'employee-edit-view component: editemployee()');
        }
      );
    }
  }

  changePasswordButton() {
    this.changingPassword = true;
  }

  savePassword() {

    if (this.passwordEdit.controls.confirmPassword.value === this.passwordEdit.controls.password.value) {
      if (!this.passwordEdit.controls.password.value.valid) {
        this.newPasswordReq = new NewPasswordReq(
          this.currentUser,
          null,
          this.passwordEdit.controls.password.value
        );
        this.employeeService.savePasswordByAdmin(this.newPasswordReq).subscribe(
          () => {
            this.backClicked();
          },
          error => {
            DEBUG_LOG('Failed to save password');
            this.alertService.alertFromError(error, {}, 'employee-edit-view component: savePassword()');
          }
        );
      } else {
        this.alertService.info('Password not valid here');
      }
    } else {
      this.alertService.info('New password and confirmation password must be same');
    }

  }
}
