import { Component, OnInit } from '@angular/core';
import {NewPasswordReq} from '../../dtos/newPasswordReq';
import {type, Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EmployeeService} from '../../services/employee.service';
import {AuthService} from '../../services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {AnimalService} from '../../services/animal.service';
import {TaskService} from '../../services/task.service';
import {EnclosureService} from '../../services/enclosure.service';
import {AlertService} from '../../services/alert.service';
import {Employee} from '../../dtos/employee';
import {Location} from '@angular/common';

@Component({
  selector: 'app-employee-password-change',
  templateUrl: './employee-password-change.component.html',
  styleUrls: ['./employee-password-change.component.css']
})
export class EmployeePasswordChangeComponent implements OnInit {

  public employee: Employee;
  currentUser: string;
  date: string;
  types = type;
  passwordEdit: FormGroup;
  newPasswordReq: NewPasswordReq;

  constructor(private employeeService: EmployeeService, private authService: AuthService, private route: ActivatedRoute,
              private _location: Location, private router: Router,
              private formBuilder: FormBuilder,
              private alertService: AlertService) {
    this.passwordEdit = this.formBuilder.group({
      password: ['', Validators.compose([
        Validators.required,
        Validators.minLength(8),
        Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]+$')
      ])],
      currentPassword: [''],
      confirmPassword: ['']
    });
  }

  ngOnInit(): void {
    this.currentUser = (this.route.snapshot.paramMap.get('username'));
  }
  cancelChanges() {
      this.backClicked();
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

  savePassword() {

    if (this.passwordEdit.controls.confirmPassword.value === this.passwordEdit.controls.password.value) {
      if (!this.passwordEdit.controls.password.value.valid) {
        this.newPasswordReq = new NewPasswordReq(
          this.currentUser,
          this.passwordEdit.controls.currentPassword.value,
          this.passwordEdit.controls.password.value
        );
        this.employeeService.savePassword(this.newPasswordReq).subscribe(
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
