// The alert System is based on the tutorial: https://jasonwatmore.com/post/2020/04/30/angular-9-alert-notifications

export class Alert {
  componentId: string;
  message: string;
  type: AlertType;
  keepAfterRouteChange: boolean = false;
  dismissible: boolean = true;
  title: string = '';
  constructor(init?: Partial<Alert>) {
    Object.assign(this, init);
  }
}

export enum AlertType {
  Error = 'danger',
  Warning = 'warning',
  Success = 'success',
  Info = 'info'
}
