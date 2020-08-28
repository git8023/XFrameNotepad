export class Directory {
  static ROOT: Directory = {id: -1};

  id?: number;
  name?: string;
  path?: string;
  parent?: Directory;

  // 修改前名称
  oldName?: string;

  constructor(id?: number, name?: string, path?: string, parent?: Directory) {
    this.id = id;
    this.name = name;
    this.path = path;
    this.parent = parent;
  }
}
