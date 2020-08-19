import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {EditorConfig} from "../../cfg/editor-config";
import {catchErr, dateFmt, eachA, handleResult2, post, regexpValidator, Storages, validNgForm} from '../../util/utils';
import {HttpClient} from '@angular/common/http';
import {NzContextMenuService, NzDropdownMenuComponent, NzModalService, NzNotificationService} from 'ng-zorro-antd';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Directory} from "../../model/entity/Directory";
import {EmitType, XFrames} from "../../util/xframes";
import {Notepad} from '../../model/entity/Notepad';
import {isNullOrUndefined} from 'util';

declare var editormd: any;

// 编辑模式
type EditorModel = 0 | 1 | 2;
// 0: 预览+编辑
const EDIT_PREVIEW = 0;
// 1: 预览
const ONLY_PREVIEW = 1;
// 2: 编辑
const ONLY_EDIT = 2;


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
  // 目录列表
  dirs: Array<Directory> = [];
  // 文件列表
  notepads: Array<Notepad> = [
    {id: 1, content: '123<b>粗体</b>哈哈'}
  ];
  // 当前目录
  currentDir: Directory = Directory.ROOT;
  // 上下文菜单目录
  contextDir: Directory;
  // 上下文菜单状态
  showContextMenu = false;
  // 加载中?
  isLoading = false;
  // 打开的记事本
  openedNotepad: Notepad = {title: ''};
  // 切换编辑器模式
  editorModel: EditorModel = 0;

  constructor(
    private http: HttpClient,
    private notify: NzNotificationService,
    private modal: NzModalService,
    private fb: FormBuilder,
    private nzContextMenuService: NzContextMenuService
  ) {
    window['x'] = this;
  }

  ngOnInit(): void {
    // // TODO
    // this.init();

    // 添加目录表单
    let addDirValid = regexpValidator({regex: /^[a-zA-Z\u4e00-\u9fa5_$0-9]{2,10}$/, truth: false});
    this.addDirForm = this.fb.group({
      name: [null, [Validators.required, addDirValid]]
    });

    // 登录校验
    let userId = Storages.SESSION.user();
    if (!userId) {
      this.modal.warning({
        nzTitle: '警告',
        nzContent: '非法调用',
        nzWrapClassName: 'vertical-center-modal',
        nzOnCancel: MainComponent.exitApp,
        nzOnOk: MainComponent.exitApp
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
  static exitApp() {
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
        onFail: MainComponent.exitApp
      }));
  }

  // 初始化
  private init() {
    setTimeout(() => {
      // 初始化 editormd 插件
      this.conf.markdown = '<span>点击左侧选择文件...</span>';
      this.conf.onchange = () => {
        console.log(this.editor.getMarkdown());
      };
      this.editor = editormd('mdEditor', this.conf);
    }, 0);

    // 加载子目录
    this.reloadDirs();
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
        onOk: ({data}) => this.dirs = data || [],
        final: () => this.isLoading = false
      }));

    post(this.http, `/notepad/list/${this.currentDir.id}`, null, ...catchErr())
      .subscribe(handleResult2({
        notify: this.notify,
        onOk: ({data}) => {
          let now = new Date().getTime();
          this.notepads = eachA<Notepad>(data || [], (d: Notepad) => {
            d.lastModified = new Date(d.lastModified);
            let lm = d.lastModified.getTime();
            // 同天 hh:mm
            let diffMs = now - lm;
            const dayMs = 24 * 60 * 60 * 1000;
            if (dayMs >= diffMs) {
              d.lastModified = dateFmt(d.lastModified, 'hh:mm');
              return;
            }

            // 10天之内 10天前
            const day10 = dayMs * 10;
            if (day10 >= diffMs) {
              d.lastModified = Math.floor((diffMs + day10 - 1) / day10) + '天前';
              return;
            }

            // 其他 yyyy-MM
            d.lastModified = dateFmt(d.lastModified, 'yyyy-MM');
          });
          if (!this.openedNotepad) this.openNotepad(this.notepads[0]);
        },
        final: () => this.isLoading = false
      }));
  }

  // 设置父级目录
  setParent(parent: Directory) {
    if (parent.id != this.currentDir.id) {
      this.currentDir = parent;
      XFrames.emitType(EmitType.SUBHEAD, parent.path);
      this.reloadDirs();
    }
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
        onOk: ret => this.setParent(ret.data.dir),
        final: () => this.isLoading = false
      }));
  }

  // 打开记事本
  openNotepad(notepad: Notepad) {
    this.openedNotepad = notepad;
    this.editor.setMarkdown(notepad.content);
    setTimeout(() => this.editor.setMarkdown(notepad.content));
    if (!this.editor.state.preview)
      this.editor.previewing();
  }

  // 切换编辑器模式
  changeEditorModal(editorModel?: EditorModel) {
    this.editorModel = isNullOrUndefined(editorModel) ? <EditorModel>(++this.editorModel % 3) : editorModel;
    switch (this.editorModel) {
      case  EDIT_PREVIEW:
        // this.editor.show();
        this.editor.watch();
        break;
      case ONLY_PREVIEW:
        // this.editor.show();
        this.editor.previewing();
        setTimeout(() => this.editor.setMarkdown(this.editor.getMarkdown()), 200);
        break;
      case ONLY_EDIT:
        this.editor.previewed();
        this.editor.unwatch();
        break;
    }
  }

  // 保存
  saveNotepad() {
    this.isLoading = true;
    let param = {
      id: this.openedNotepad.id,
      content: this.editor.getMarkdown(),
      title: this.openedNotepad.title
    };
    post(this.http, `/notepad/update/`, param, ...catchErr())
      .subscribe(handleResult2({
        notify: this.notify,
        onOk: () => this.changeEditorModal(ONLY_PREVIEW),
        final: () => this.isLoading = false
      }));
  }
}
