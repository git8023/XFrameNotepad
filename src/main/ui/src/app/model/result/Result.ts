export class Result {
  flag: boolean;
  message: string;
  data: any;
  errorCode?: any;

  static readonly fail = new Result(false, '服务器忙, 请稍候再试.');
  static readonly success = new Result(true, '操作成功');
  static readonly timeout = new Result(false, '请求超时');

  constructor(flag?: boolean, message?: string, data?: object, errorCode?: any) {
    this.flag = flag;
    this.message = message;
    this.data = data;
    this.errorCode = errorCode;
  }
}
