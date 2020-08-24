import {Directive, HostListener, Injectable} from '@angular/core';
import {NgbTimeAdapter, NgbTimeStruct} from '@ng-bootstrap/ng-bootstrap';

@Injectable({
  providedIn: 'root'
})
export class Globals {
  readonly backendUri: string = 'http://localhost:8080/api/v1';
}
export enum type {
  ANIMAL_CARE = 'ANIMAL_CARE',
  DOCTOR = 'DOCTOR',
  JANITOR = 'JANITOR'
}
export namespace Utilities {
  export const DEBUG_MODE = false;
  export const DEBUG_LOG = DEBUG_MODE ? console.log.bind(console) : function () {};
}
export enum TimeUnits {
  DAY = 'DAYS',
  WEEK = 'WEEKS',
  MONTH = 'MONTHS',
  YEAR = 'YEARS'
}

const pad = (i: number): string => i < 10 ? `0${i}` : `${i}`;

/**
 * From https://ng-bootstrap.github.io/#/components/timepicker/examples#adapter
 */
@Injectable()
export class NgbTimeStringAdapter extends NgbTimeAdapter<string> {

  fromModel(value: string| null): NgbTimeStruct | null {
    if (!value) {
      return null;
    }
    const split = value.split(':');
    return {
      hour: parseInt(split[0], 10),
      minute: parseInt(split[1], 10),
      second: parseInt(split[2], 10)
    };
  }

  toModel(time: NgbTimeStruct | null): string | null {
    return time != null ? `${pad(time.hour)}:${pad(time.minute)}:${pad(time.second)}` : null;
  }
}
