import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {EditorConfig} from "../../cfg/editor-config";
import {catchErr, handleResult2, post, regexpValidator, Storages, validNgForm} from '../../util/utils';
import {HttpClient} from '@angular/common/http';
import {NzContextMenuService, NzDropdownMenuComponent, NzModalService, NzNotificationService} from 'ng-zorro-antd';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Directory} from "../../model/entity/Directory";
import {EmitType, XFrames} from "../../util/xframes";

declare var editormd: any;

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {

  @ViewChild('content', {static: true, read: ElementRef}) content: ElementRef;
  // markdown编辑器配置
  conf = new EditorConfig();
  // markdown编辑器实例
  editor: any;
  // 新建文件/目录显示?
  addModalVisible = false;
  // 添加目录表单
  addDirForm: FormGroup;
  // // 当前父级目录ID
  // parentId = -1;
  // 目录列表
  dirs: Array<Directory> = [
    // TODO
    {id: 1},
    {id: 2},
  ];
  // 当前目录
  currentDir: Directory = Directory.ROOT;
  // 上下文菜单目录
  contextDir: Directory;
  // 上下文菜单状态
  showContextMenu = false;
  // 加载中?
  isLoading = false;

  constructor(
    private http: HttpClient,
    private notify: NzNotificationService,
    private modal: NzModalService,
    private fb: FormBuilder,
    private nzContextMenuService: NzContextMenuService
  ) {
  }

  ngOnInit(): void {
    // TODO
    // this.init();

    let addDirValid = regexpValidator({regex: /^[a-zA-Z\u4e00-\u9fa5_$0-9]{2,10}$/, truth: false});
    this.addDirForm = this.fb.group({
      name: [null, [Validators.required, addDirValid]]
    });

    let userId = Storages.SESSION.user();
    if (!userId) {
      this.modal.warning({
        nzTitle: '警告',
        nzContent: '非法调用',
        nzWrapClassName: 'vertical-center-modal',
        nzOnCancel: this.exitApp,
        nzOnOk: this.exitApp
      });
      return;
    }

    this.login();
    // 获取 md 内容
    // editor.getMarkdown()
    // 解析 md 内容
    // editormd.markdownToHTML('detailmarkdown', this.conf);
  }

  // 退出应用
  exitApp() {
    XFrames.emitType(EmitType.EXIT);
  }

  // 新建文件夹
  addDir() {
    if (!validNgForm(this.addDirForm))
      return;

    this.isLoading = true;
    let parentId = (this.contextDir || this.currentDir).id;
    let url = `/dir/add/${this.addDirForm.value.name}/${parentId}`;
    post(this.http, url, null, ...catchErr())
      .subscribe(handleResult2({
        notify: this.notify,
        onOk: () => {
          this.reloadDirs();
          this.addModalVisible = false;
          this.closeContextMenu();
        },
        final: () => this.isLoading = false
      }));
  }

  // 登录
  private login() {
    let userId = Storages.SESSION.user();
    post(this.http, `/user/login`, {userId}, ...catchErr())
      .subscribe(handleResult2({
        notify: this.notify,
        onOk: ({data}) => {
          Storages.SESSION.user(data);
          this.init();
        },
        onFail: this.exitApp
      }));
  }

  // 初始化
  private init() {

    // 加载子目录
    this.reloadDirs();

    this.conf.markdown = "测试代码 <b>粗体</b>";
    this.conf.onchange = () => {
      console.log(this.editor.getMarkdown());
    };
    this.editor = editormd('mdEditor', this.conf);

  }

  // 显示添加目录弹出框
  showAddDirModal() {
    this.addModalVisible = true;
    this.addDirForm.reset();
  }

  // 关闭添加目录窗口
  hideAddDirModal() {
    this.addModalVisible = false;
    this.closeContextMenu();
  }

  // 加载目录和文件
  private reloadDirs() {
    this.isLoading = true;
    post(this.http, `/dir/dirs/${this.currentDir.id}`, null, ...catchErr())
      .subscribe(handleResult2({
        notify: this.notify,
        onOk: ret => {
          this.dirs = ret.data || [];
        },
        final: () => this.isLoading = false
      }));
  }

  // 设置父级目录
  setParent(parent: Directory) {
    this.currentDir = parent;
    XFrames.emitType(EmitType.SUBHEAD, parent.path);
    this.reloadDirs();
  }

  // 返回上级目录
  intoParentDir() {
    let parent = this.currentDir.parent;
    this.setParent(parent ? parent : Directory.ROOT);
  }

  // 在目录上点击右键
  dirContextMenu($event: MouseEvent, menu: NzDropdownMenuComponent, dir: Directory) {
    this.contextDir = dir;
    this.showContextMenu = true;
    this.nzContextMenuService.create($event, menu);
  }

  // 关闭上下文菜单
  closeContextMenu() {
    this.nzContextMenuService.close();
    this.contextDir = null;
    this.showContextMenu = false;
  }

  // 添加文件
  addFile() {
    this.isLoading = true;
    let dirId = (this.contextDir || this.currentDir).id;
    post(this.http, `/notepad/newBlank/${dirId}`, null, ...catchErr())
      .subscribe(handleResult2({
        notify: this.notify,
        onOk: ret => {
          console.log('文件创建成功', ret);
        },
        final: () => this.isLoading = false
      }));
  }

}
