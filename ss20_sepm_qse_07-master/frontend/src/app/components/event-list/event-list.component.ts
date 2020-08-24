import {Component, OnInit} from '@angular/core';
import {Task} from '../../dtos/task';
import {type, Utilities} from '../../global/globals';
import {AuthService} from '../../services/auth.service';
import {TaskService} from '../../services/task.service';
import {AlertService} from '../../services/alert.service';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Component({
  selector: 'app-event-list',
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.css']
})
export class EventListComponent implements OnInit {
  componentId = 'event-list';

  selected: Task;

  tasks: Task[];
  filterTask: Task;

  constructor(private authService: AuthService, private taskService: TaskService,
              private alertService: AlertService) {
  }

  ngOnInit(): void {
    this.filterTask = new Task(null, null, null, null,
      null, null, null, null, null, null, null);
    this.loadFilteredTasks();
  }

  loadFilteredTasks() {
    this.filterTask.startTime = this.parseDate(this.filterTask.startTime);
    this.taskService.searchEvents(this.filterTask).subscribe(
      (tasks) => {
        this.tasks = tasks;
      },
      error => {
        DEBUG_LOG('Error loading events!');
        this.alertService.alertFromError(error,  {}, 'Event List component: loadFilteredTasks()');
      }
    );
  }

  parseDate(dateUnparsed) {
    if (dateUnparsed === null) {
      return null;
    }
    const parsed = new Date(dateUnparsed);
    if (Number.isNaN(parsed.getFullYear()) || Number.isNaN(parsed.getMonth()) || Number.isNaN(parsed.getDate())) {
      return null;
    }
    const year = parsed.getFullYear();
    const month = parsed.getMonth() + 1;
    const day = parsed.getDate();
    const monthZero = (month < 10) ? '0' : '';
    const dayZero = (day < 10) ? '0' : '';
    const date = year + '-' + monthZero + month + '-' + dayZero + day;

    return date;
  }

  display(task: Task) {
    if(this.selected === task) {
      this.selected = null;
    } else {
      this.selected = task;
    }
  }
}
