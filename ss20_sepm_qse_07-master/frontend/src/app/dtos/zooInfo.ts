import {Time} from '@angular/common';

export class ZooInfo {
  constructor(
    public id: number,
    public name: string,
    public address: string,
    public publicInfo: string,
    public workTimeStart: Time,
    public workTimeEnd: Time,
    public picture: string) {
  }
}
