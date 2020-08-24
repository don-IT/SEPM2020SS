import {TimeUnits} from '../global/globals';

export class RepeatableTask {
  constructor(public id: number,
              public title: string,
              public description: string,
              public startTime: string,
              public endTime: string,
              public assignedEmployeeUsername: string,
              public status: string,
              public subjectId: string,
              public subjectName: string,
              public animalTask: boolean,
              public priority: boolean = false,
              public amount: number,
              public separation: TimeUnits,
              public separationAmount: number,
              public event: boolean = false,
              public publicInfo: string = null,
              public eventPicture: string = null) {
  }
}
