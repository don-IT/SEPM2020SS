import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {EmployeeService} from '../../services/employee.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {


  constructor(public employeeService: EmployeeService, public authService: AuthService, private router: Router) {
  }

  ngOnInit() {
  }

  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

}
