import {Component, OnInit} from '@angular/core';
import {FullCalendarComponent} from '@fullcalendar/angular';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import {EmployeeService} from '../../services/employee.service';
import {AuthService} from '../../services/auth.service';
import {TaskService} from '../../services/task.service';
import {EnclosureService} from '../../services/enclosure.service';
import {AlertService} from '../../services/alert.service';
import {Employee} from '../../dtos/employee';
import {Task} from '../../dtos/task';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Component({
  selector: 'app-calender',
  templateUrl: './calender.component.html',
  styleUrls: ['./calender.component.css']
})
export class CalenderComponent implements OnInit {

  currentEmployee: Employee;

  calendarPlugins = [dayGridPlugin, timeGridPlugin];
  header = {
    left: 'prev,next today',
    center: 'title',
    right: 'timeGridDay,timeGridWeek,dayGridMonth'
  };
  tasks;

  constructor(private employeeService: EmployeeService, private authService: AuthService, private taskService: TaskService,
              private alertService: AlertService) {
  }

  ngOnInit(): void {
    if (this.authService.getUserRole() === 'ADMIN') {
      this.alertService.warn('As an admin you do not have a calender', {}, 'Calender');
    } else {
      this.loadPersonalInfo();
    }
  }

  parseTaskToFullCalenderEvent(task: Task) {
    return {
      title: task.title,
      start: task.startTime,
      end: task.endTime
    };
  }

  loadPersonalInfo() {
    this.employeeService.getPersonalInfo().subscribe(
      (employee: Employee) => {
        this.currentEmployee = employee;
        this.loadTasksOfEmployee();
      },
      error => {
        this.alertService.alertFromError(error, {}, 'Calender: loadPersonInfo');
      }
    );
  }

  loadTasksOfEmployee() {
    this.taskService.getTasksOfEmployee(this.currentEmployee.username).subscribe(
      (tasks) => {
        this.tasks = tasks.map(x => this.parseTaskToFullCalenderEvent(x));
        DEBUG_LOG(JSON.stringify(this.tasks));
      },
      error => {
        this.alertService.alertFromError(error, {}, 'Calender: loadTaskOfEmployee');
      }
    );

  }
}
