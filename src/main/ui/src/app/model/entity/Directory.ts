export class Directory {
  static ROOT: Directory = {id: -1};

  id?: number;
  name?: string;
  path?: string;
  parent?: Directory;

}
