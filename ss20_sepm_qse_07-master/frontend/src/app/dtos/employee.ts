import {type} from '../global/globals';
import {Time} from '@angular/common';

export class Employee {
  constructor(
    public username: string,
    public email: string,
    public password: string,
    public name: string,
    public birthday: Date,
    public workTimeStart: Time,
    public workTimeEnd: Time,
    public type: type) {
  }
}
