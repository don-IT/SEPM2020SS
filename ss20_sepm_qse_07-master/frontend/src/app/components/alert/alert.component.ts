import {Component, Input, OnInit, OnDestroy} from '@angular/core';
import {Alert, AlertType} from '../../dtos/alert';
import {Subscription} from 'rxjs';
import {NavigationStart, Router} from '@angular/router';
import {AlertService} from '../../services/alert.service';
import {Utilities} from '../../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit, OnDestroy {
  @Input() alertComponentId = 'default-alert';


  alerts: Alert[] = [];

  alertSubscription: Subscription;
  routeSubscription: Subscription;
  DEBUG_MODE = Utilities.DEBUG_MODE;

  constructor(private router: Router, private alertService: AlertService) {
  }

  ngOnInit(): void {
    this.alerts = this.alertService.getAlerts(this.alertComponentId);
    this.alertSubscription = this.alertService.onAlert(this.alertComponentId)
      .subscribe(
        alert => {
          if (!alert.message) {
            this.alerts = this.alerts.filter(al => al.keepAfterRouteChange);

            // Uncomment next line to only keep after one Route Change
            // this.alerts.forEach(al => delete al.keepAfterRouteChange);
            return;
          }

          //
          this.alerts = this.alerts.filter((al) => !(al.message === alert.message && al.type === alert.type));
          this.alerts.push(alert);
        }
      );

    this.routeSubscription = this.router.events
      .subscribe(
        event => {
          if (event instanceof NavigationStart) {
            this.alertService.clear(this.alertComponentId);
          }
        }
    );

  }

  ngOnDestroy() {
    // unsubscribe to avoid memory leaks
    this.alertSubscription.unsubscribe();
    this.routeSubscription.unsubscribe();
  }

  consoleExistingAlerts() {
    DEBUG_LOG(this.alertService.getAlerts(this.alertComponentId));
  }

  closeAlert(alert: Alert) {
    this.alerts.splice(this.alerts.indexOf(alert), 1);
    this.alertService.removeAlert(alert);
  }

  clearAlerts() {
    this.alertService.clear(this.alertComponentId);
    this.alerts.length = 0;
  }
}
