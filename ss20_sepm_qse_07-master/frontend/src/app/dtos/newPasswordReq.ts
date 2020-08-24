export class NewPasswordReq {
  constructor(
    public username: string,
    public currentPassword: string,
    public newPassword: string
  ) {}
}
