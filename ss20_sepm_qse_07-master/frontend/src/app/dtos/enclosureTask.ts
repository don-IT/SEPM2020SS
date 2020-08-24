export class EnclosureTask {
  constructor(public id: number,
              public title: string,
              public description: string,
              public startTime: string,
              public endTime: string,
              public assignedEmployeeUsername: string,
              public status: string,
              public enclosureId: string,
              public enclosureName: string,
              public priority: boolean = false,
              public event: boolean = false,
              public publicInfo: string = null,
              public eventPicture: string = null
  ) {
  }
}
