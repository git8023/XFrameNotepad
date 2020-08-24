import {Directory} from './Directory';

export class Notepad {
  id?: number;
  title?: string;
  content?: string;
  creator?: any;
  dir?: Directory;
  createTime?: Date;
  lastModified?: Date | any;
  size?: number;
  type?: string;

  static EMPTY: Notepad = {id: -1, title: '', content: ''};
}
