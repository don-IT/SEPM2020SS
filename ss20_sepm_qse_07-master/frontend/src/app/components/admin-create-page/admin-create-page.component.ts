import { Component, OnInit } from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {AlertService} from '../../services/alert.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthRequest} from '../../dtos/auth-request';
import DEBUG_LOG = Utilities.DEBUG_LOG;
import {Utilities} from '../../global/globals';
import {AdminService} from '../../services/admin.service';

@Component({
  selector: 'app-admin-create-page',
  templateUrl: './admin-create-page.component.html',
  styleUrls: ['./admin-create-page.component.css']
})
export class AdminCreatePageComponent implements OnInit {
  adminCreationForm: FormGroup;
  submittedAdmin: boolean;

  constructor(private adminService: AdminService, private authService: AuthService, private route: Router, private alertService: AlertService, private formBuilder: FormBuilder) {
    this.adminCreationForm = this.formBuilder.group({
      username: ['', [Validators.required] ],
      password: ['', Validators.compose([
        Validators.required,
        Validators.minLength(8),
        Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]+$')
      ])]
    });
  }

  ngOnInit(): void {
    this.submittedAdmin = false;
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  addAdmin() {
    this.submittedAdmin = true;
    if (this.adminCreationForm.valid) {
      const authRequest: AuthRequest = new AuthRequest(
        this.adminCreationForm.controls.username.value,
        this.adminCreationForm.controls.password.value,
      );
      this.adminService.createAdmin(authRequest).subscribe(
        (res: any) => {
          DEBUG_LOG('Admin Created');
        },
        error => {
          this.alertService.alertFromError(error,  {}, 'createAdmin');
        }
      );
      this.clearForm();
    } else {
      DEBUG_LOG('Invalid Input');
    }

  }

  private clearForm() {
    this.adminCreationForm.reset();
    this.submittedAdmin = false;
  }
}
