export class Comment {
  constructor(
    public id: number,
    public taskId: number,
    public creatorUsername: string,
    public timeStamp: string,
    public comment: string) {
  }
}
