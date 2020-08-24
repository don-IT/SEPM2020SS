import {Injectable} from '@angular/core';
import {Alert, AlertType} from '../dtos/alert';
import {Observable, Subject} from 'rxjs';
import {filter} from 'rxjs/operators';
import {Globals, Utilities} from '../global/globals';
import DEBUG_LOG = Utilities.DEBUG_LOG;

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  private subject = new Subject<Alert>();
  private defaultId = 'default-alert';

  alerts: Alert[] = [];


  constructor() { }

  onAlert(alertComponentId = this.defaultId): Observable<Alert> {
    DEBUG_LOG('Subscribed to alerts with component: ' + alertComponentId);
    return this.subject.asObservable().pipe(filter(al => al && al.componentId === alertComponentId));
  }

  getAlerts(alertComponentId = this.defaultId) {
    return this.alerts.filter(alert => alert && alert.componentId === alertComponentId);
  }

  /**
   * Will create an Alert that will be Displayed through the alert system
   * @param alert the Alert to display
   * @param sourceFn the function where this Alert was thrown
   */
  alert(alert: Alert, sourceFn: string = 'No sourceFn given') {
    alert.componentId = alert.componentId || this.defaultId;

    DEBUG_LOG(alert.type + ' Alert for ComponentID: ' + alert.componentId + ' message: ' + alert.message + ' routeChange '
      + alert.keepAfterRouteChange);

    this.subject.next(alert);
    this.alerts.push(alert);
  }

  /**
   * Will create an Alert that will be Displayed through the alert system
   * @param message the message for the alert to display
   * @param sourceFn the Function in which the alertWasIssued. This will just be shown in the console in debug mode.
   * @param options type: AlertType, this will override the Type Extracted from the error.\n
   * keepAfterRouteChange: boolean, if true, the alert will stay when navigating to a different page that also has the alert component. \n
   * dismissible: boolean, if true, the alert will have an X button the remove the alert, otherwise it can't be removed by the user.
   * componentId: string Id of the ErrorComponent that will show the Error Message by default this is the global default-alert-component.
   * It can be set through \@Input alertComponentId when you want a custom alert component
   */
  success(message: string, options?: any, sourceFn: string = 'No sourceFn given') {
    this.alert(new Alert({ ...options, type: AlertType.Success, message }), sourceFn);
  }

  /**
   * Will create an Alert that will be Displayed through the alert system
   * @param message the message for the alert to display
   * @param sourceFn the Function in which the alertWasIssued. This will just be shown in the console in debug mode.
   * @param options type: AlertType, this will override the Type Extracted from the error.\n
   * keepAfterRouteChange: boolean, if true, the alert will stay when navigating to a different page that also has the alert component. \n
   * dismissible: boolean, if true, the alert will have an X button the remove the alert, otherwise it can't be removed by the user.
   * componentId: string Id of the ErrorComponent that will show the Error Message by default this is the global default-alert-component.
   * It can be set through \@Input alertComponentId when you want a custom alert component
   */
  error(message: string, options?: any, sourceFn: string = 'No sourceFn given') {
    this.alert(new Alert({ ...options, type: AlertType.Error, message }), sourceFn);
  }

  /**
   * Will create an Alert that will be Displayed through the alert system
   * @param message the message for the alert to display
   * @param sourceFn the Function in which the alertWasIssued. This will just be shown in the console in debug mode.
   * @param options type: AlertType, this will override the Type Extracted from the error.\n
   * keepAfterRouteChange: boolean, if true, the alert will stay when navigating to a different page that also has the alert component. \n
   * dismissible: boolean, if true, the alert will have an X button the remove the alert, otherwise it can't be removed by the user.
   * componentId: string Id of the ErrorComponent that will show the Error Message by default this is the global default-alert-component.
   * It can be set through \@Input alertComponentId when you want a custom alert component
   */
  info(message: string, options?: any, sourceFn: string = 'No sourceFn given') {
    this.alert(new Alert({ ...options, type: AlertType.Info, message }), sourceFn);
  }

  /**
   * Will create an Alert that will be Displayed through the alert system
   * @param message the message for the alert to display
   * @param sourceFn the Function in which the alertWasIssued. This will just be shown in the console in debug mode.
   * @param options type: AlertType, this will override the Type Extracted from the error.\n
   * keepAfterRouteChange: boolean, if true, the alert will stay when navigating to a different page that also has the alert component. \n
   * dismissible: boolean, if true, the alert will have an X button the remove the alert, otherwise it can't be removed by the user.
   * componentId: string Id of the ErrorComponent that will show the Error Message by default this is the global default-alert-component.
   * It can be set through \@Input alertComponentId when you want a custom alert component
   */
  warn(message: string, options?: any, sourceFn: string = 'No sourceFn given') {
    this.alert(new Alert({ ...options, type: AlertType.Warning, message }), sourceFn);
  }

  /**
   * Will create an Alert that will be Displayed through the alert system
   * @param error the Error that of which to extract the message an Alert Status (e.g. 404 is warning).
   * @param sourceFn the Function in which the alertWasIssued. This will just be shown in the console in debug mode.
   * @param options type: AlertType, this will override the Type Extracted from the error.\n
   * keepAfterRouteChange: boolean, if true, the alert will stay when navigating to a different page that also has the alert component. \n
   * dismissible: boolean, if true, the alert will have an X button the remove the alert, otherwise it can't be removed by the user.
   * componentId: string Id of the ErrorComponent that will show the Error Message by default this is the global default-alert-component.
   * It can be set through \@Input alertComponentId when you want a custom alert component
   */
  alertFromError(error: any, options?: any, sourceFn: string = 'No sourceFn given') {
    DEBUG_LOG('Alerting for Error of component: ' + options.componentId + ' from sourceFn: ' + sourceFn);
    DEBUG_LOG(error);
    let type = AlertType.Error;
    let message: string = 'Sorry, something went wrong on our end. We will try to fix it as soon as possible. If you want to help us tell us' +
      'what you did before this message appeared.';
    if (typeof error.error === 'object') {
      if ((error.error !== null && error.error !== undefined) && error.error.status !== undefined) {
        if (error.error.status === 404) {
          type = AlertType.Warning;
        }
      }
      if (error.error !== null && error.error !== undefined) {
        message = error.error.error;
      }
    } else {
      if (error.status !== undefined) {
        if (error.status === 404) {
          type = AlertType.Warning;
        }
      }

      message = error.error;

    }

    this.alert(new Alert({ ...options, message: message, type: type}), sourceFn);
  }

  clear(correspondingAlertComponentId = this.defaultId) {
    this.subject.next(new Alert({componentId: correspondingAlertComponentId} ));
    this.alerts = this.alerts.filter(al => al.componentId !== correspondingAlertComponentId);
  }

  removeAlert(alert: Alert) {
    this.alerts.splice(this.alerts.indexOf(alert), 1);
  }
}
