import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {EditorConfig, initPasteDragImg} from "../../cfg/editor-config";
import {
  catchErr,
  clone,
  Counter,
  dateFmt,
  Debugger,
  eachA,
  extendPropsA, findA,
  groupA,
  handleResult2,
  post,
  regexpValidator,
  Storages,
  validNgForm
} from '../../util/utils';
import {HttpClient} from '@angular/common/http';
import {
  NzContextMenuService,
  NzDropdownMenuComponent,
  NzMessageService,
  NzModalService,
  NzNotificationService
} from 'ng-zorro-antd';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Directory} from "../../model/entity/Directory";
import {EmitType, XFrames2} from "../../util/Xframes2";
import {Notepad} from '../../model/entity/Notepad';
import {isFunction, isNullOrUndefined} from 'util';
import {isNotNullOrUndefined} from 'codelyzer/util/isNotNullOrUndefined';
import {fromEvent, of} from 'rxjs';
import {debounceTime, distinctUntilChanged, pluck, switchMap} from 'rxjs/operators';
import {Result} from '../../model/result/Result';
import {Recycle} from "../../model/entity/Recycle";

declare var editormd: any;

// 编辑模式
type EditorModel = 0 | 1 | 2;
// 0: 预览+编辑
const EDIT_PREVIEW = 0;
// 1: 预览
const ONLY_PREVIEW = 1;
// 2: 编辑
const ONLY_EDIT = 2;

// 左侧菜单
class LeftMenu {
  // nz样式类型
  nzType: string;

  // 显示的文本内容
  text: string;

  // 点击处理事件
  click?: Function;

  // 点击菜单后是否保留痕迹? 激活active样式
  // 默认 false
  trail?: boolean;

  // 按钮关键字
  type: 'LATELY' | 'MINE_FOLDER' | 'SHARED_TO_ME' | 'MINE_SHARED' | 'RECYCLE' | 'EXIT';

  // 删除文件特殊控制
  deleteNotepad?: (notepad: Notepad) => void
}

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {
  @ViewChild('search', {static: true, read: ElementRef}) searchKeyElRef: ElementRef;

  // 最左侧滑动菜单
  leftMenus: Array<LeftMenu> = [
    {
      nzType: 'snippets',
      text: '最近文档',
      trail: true,
      click: () => this.latelyNotepad(),
      type: 'LATELY',
      deleteNotepad: notepad => {
        this.isLoading = true;
        Debugger
          .simulate<Result>({flag: true})
          .post(this.http, `/notepad/del/${notepad.id}`)
          .subscribe(handleResult2({
            showSuccess: true,
            notify: this.notify,
            onOk: () => {
              this.latelyNotepad();
              this.checkOpenedNotepadExist();
            },
            final: () => {
              this.isLoading = false;
              this.closeContextMenu();
            }
          }));
      }
    },
    {
      nzType: 'folder',
      text: '我的文件夹',
      trail: true,
      click: () => this.setParent(Directory.ROOT),
      type: 'MINE_FOLDER',
      deleteNotepad: notepad => {
        this.isLoading = true;
        Debugger
          .simulate<Result>({flag: true})
          .post(this.http, `/notepad/del/${notepad.id}`)
          .subscribe(handleResult2({
            showSuccess: true,
            notify: this.notify,
            onOk: () => {
              this.reloadDirsAndNotepads();
              this.checkOpenedNotepadExist();
            },
            final: () => {
              this.isLoading = false;
              this.closeContextMenu();
            }
          }));
      }
    },
    // {nzType: 'deployment-unit', text: '与我分享', trail: true, type: 'SHARED_TO_ME'},
    // {nzType: 'share-alt', text: '我的分享', trail: true, type: 'MINE_SHARED'},
    {
      nzType: 'delete',
      text: '回收站',
      trail: true,
      click: () => this.listRecycles(),
      type: 'RECYCLE',
      deleteNotepad: notepad => {
        this.isLoading = true;
        Debugger
          .simulate<Result>({flag: true})
          .post(this.http, `/notepad/recycleDel/${notepad.id}`)
          .subscribe(handleResult2({
            notify: this.notify,
            onOk: () => this.listRecycles(),
            final: () => {
              this.isLoading = false;
              this.closeContextMenu();
            }
          }));
      }
    },
    {nzType: 'logout', text: '退出', click: () => this.exitApp(), type: 'EXIT'}
  ];

  // 当前激活的左侧菜单
  activeLeftMenu: LeftMenu = this.leftMenus[0];

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
  notepads: Array<Notepad> = [];

  // 当前目录
  currentDir: Directory = Directory.ROOT;

  // 上下文菜单目录
  contextDir: Directory;

  // 上下文菜单状态
  // true-打开的
  // false-关闭的
  showContextMenu = false;

  // 加载中
  // true-显示加载状态
  // false-隐藏加载状态
  isLoading = false;

  // 打开的记事本
  openedNotepad: Notepad = clone(Notepad.EMPTY);

  // 切换编辑器模式
  editorModel: EditorModel = 0;

  // 目录名称校验器
  dirNameValidator = regexpValidator({regex: /^[a-zA-Z\u4e00-\u9fa5_$0-9]{2,10}$/, truth: false});

  // 激活右键菜单的记事本
  contextNotepad: Notepad;

  // 记事本移动到其他目录弹出框
  // true-显示
  // false-隐藏
  notepadMovingModalVisible = false;

  // 移动中选中的目录
  chooseDirId: number = Directory.ROOT.id;

  // 当前用户下所有的目录
  allDirs = [];

  // 搜索关键字
  searchKey: string = '';

  constructor(
    private http: HttpClient,
    private notify: NzNotificationService,
    private modal: NzModalService,
    private fb: FormBuilder,
    private msgService: NzMessageService,
    private nzContextMenuService: NzContextMenuService
  ) {
  }

  ngOnInit(): void {

    // 添加目录表单
    this.addDirForm = this.fb.group({
      name: [null, [Validators.required, this.dirNameValidator]]
    });

    // 监听搜索框
    fromEvent(this.searchKeyElRef.nativeElement, 'keyup')
      .pipe(
        debounceTime(300),
        pluck('target', 'value'),
        distinctUntilChanged(),
        switchMap(key => {
          return Debugger.getValue(
            Debugger.devVal(() => of({flag: true, data: [{id: 1, name: 'test'}]})),
            Debugger.prodVal(() => {
              if (key) return post(this.http, `/search/cup`, {key});
              else this.reloadDirsAndNotepads();
            })
          );
        }),
      )
      .subscribe((data: Result) => {
        console.log('收到数据: ', data);
        if (data) {
          let typeGroup = groupA(data.data as any[], 'type');
          this.dirs = extendPropsA(typeGroup['DIRECTORY'], 'data');
          this.notepads = extendPropsA(typeGroup['NOTEPAD'], 'data');
        }
      });

    Debugger
      .dev(() => this.init())
      .prod(() => {
        // 登录校验
        let userId = Storages.SESSION.user();
        if (!userId) {
          this.modal.warning({
            nzTitle: '警告',
            nzContent: '非法调用',
            nzWrapClassName: 'vertical-center-modal',
            nzOnCancel: this.exist,
            nzOnOk: this.exist
          });
          return;
        }
        this.login();
      });
  }

  // 退出
  exist() {
    XFrames2.emitType(EmitType.EXIT);
  }

  // 退出应用
  exitApp() {
    this.modal.confirm({
      nzTitle: '退出提示',
      nzContent: '确定要退出当前应用吗?',
      nzOkText: '退出',
      nzCancelText: '取消',
      nzOnOk: this.exist
    });
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
          this.reloadDirsAndNotepads();
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
    this.resetEditorMd();
    this.latelyNotepad();
  }

  // 重置 editormd 插件
  private resetEditorMd() {
    setTimeout(() => {
      this.openedNotepad = clone(Notepad.EMPTY);
      // 初始化 editormd 插件
      this.conf.markdown = '点击左侧选择文件...';
      this.conf.onchange = () => {
        console.log(this.editor.getMarkdown());
      };
      this.conf.onload = () => initPasteDragImg(this.editor);

      if (!this.editor)
        this.editor = editormd('mdEditor', this.conf);
      else
        this.editor.setMarkdown(this.conf.markdown);
      this.changeEditorModal(ONLY_EDIT);
      setTimeout(() => this.changeEditorModal(ONLY_PREVIEW), 500);
    }, 0);
  }

  // 显示添加目录弹出框
  showAddDirModal() {
    this.addModalVisible = true;
    this.addDirForm.reset();
  }

  // 显示修改目录名称弹出框
  showModifyDirModal() {
    let name = this.contextDir.name;
    this.contextDir.oldName = name;
    this.showAddDirModal();
    this.addDirForm.controls.name.setValue(name);
  }

  // 关闭添加目录窗口
  hideAddDirModal() {
    this.addModalVisible = false;
    this.closeContextMenu();
  }

  // 加载目录和文件
  private reloadDirsAndNotepads() {
    this.isLoading = true;
    new Counter()
      .fire(timer => {
        Debugger
          .simulate<Result>({flag: true, data: Directory.ROOT})
          .post(this.http, `/dir/detail/${this.currentDir.id}`)
          .subscribe(handleResult2({
            notify: this.notify,
            onOk: ({data}) => this.currentDir = data || Directory.ROOT,
            final: () => {
              XFrames2.emitType(EmitType.SUBHEAD, this.currentDir.path);
              timer();
            }
          }));
      })
      .fire(timer => {
        Debugger
          .simulate<Result>({flag: true, data: <Directory[]>[{id: 1, name: 'Dir-1', path: '/Dir1'}]})
          .post(this.http, `/dir/dirs/${this.currentDir.id}`)
          .subscribe(handleResult2({
            notify: this.notify,
            onOk: ({data}) => this.dirs = data || [],
            final: timer
          }));
      })
      .fire(timer => {
        Debugger
          .simulate({flag: true, data: <Notepad[]>[{id: 1, title: 'Notepad-1', content: '模拟内容'}]})
          .post(this.http, `/notepad/list/${this.currentDir.id}`)
          .subscribe(handleResult2({
            notify: this.notify,
            onOk: ({data}) => this.setNotepads(data),
            final: timer
          }));
      })
      .done(() => this.isLoading = false);
  }

  // 设置父级目录
  setParent(parent: Directory) {
    this.currentDir = parent || Directory.ROOT;
    this.reloadDirsAndNotepads();
  }

  // 返回上级目录
  intoParentDir() {
    this.setParent(this.currentDir.parent);
  }

  // 在目录上点击右键
  dirContextMenu($event: MouseEvent, menu: NzDropdownMenuComponent, dir: Directory) {
    this.contextDir = dir;
    this.showContextMenu = true;
    this.nzContextMenuService.create($event, menu);
  }

  // 关闭上下文菜单
  // 1. 清理nz上下文菜单
  // 2. 清理右键目录记录
  // 3. 清理右键记事本记录
  // 4. 隐藏自定义上下文遮罩
  closeContextMenu() {
    this.nzContextMenuService.close();
    this.contextDir = null;
    this.contextNotepad = null;
    this.showContextMenu = false;
  }

  // 添加文件
  addFile() {
    this.isLoading = true;
    let dirId = (this.contextDir || this.currentDir).id;
    post(this.http, `/notepad/newBlank/${dirId}`)
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
    this.changeEditorModal(ONLY_PREVIEW);
  }

  // 切换编辑器模式
  // 1. 还原到初始状态
  // 2. 切换到目标状态
  changeEditorModal(editorModel?: EditorModel) {
    let initOpts = {
      watching: state => state && this.editor.watch(),
      loaded: state => state,
      preview: state => state && this.editor.previewing(),
      fullscreen: state => state && this.editor.previewed()
    };
    eachA<boolean>(this.editor.state, (state, key) => {
      let fn = initOpts[key];
      if (isFunction(fn))
        fn(state);
    });

    this.editorModel = isNullOrUndefined(editorModel) ? <EditorModel>(++this.editorModel % 3) : editorModel;
    switch (this.editorModel) {
      case EDIT_PREVIEW:
        this.editor.watch();
        break;
      case ONLY_PREVIEW:
        if (!this.editor.state.preview) {
          this.editor.previewing();
          setTimeout(() => this.editor.setMarkdown(this.editor.getMarkdown()), 200);
        }
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
        showSuccess: true,
        final: () => this.isLoading = false
      }));
  }

  // 更新目录名称
  updateDirName() {
    // 名称没有修改不用提交
    let newlyName = this.addDirForm.value.name;
    if (this.contextDir.oldName == newlyName) return;

    this.isLoading = true;
    let param = {id: this.contextDir.id, name: newlyName};
    post(this.http, `/dir/updateName`, param, ...catchErr())
      .subscribe(handleResult2({
        notify: this.notify,
        onOk: () => {
          this.contextDir.name = this.contextDir.oldName;
          this.addModalVisible = false;
          this.reloadDirsAndNotepads();
        },
        final: () => {
          this.isLoading = false;
          this.contextDir.oldName = null;
          this.closeContextMenu();
        }
      }));
  }

  // 处理目录修改事件
  handleModifyDir() {
    if (this.contextDir && isNotNullOrUndefined(this.contextDir.oldName))
      this.updateDirName();
    else
      this.addDir();
  }

  // 删除目录
  deleteDir() {
    new Promise((resolve, reject) => {
      this.modal.confirm({
        nzTitle: '删除提示',
        nzContent: `确定要删除目录 <b>${this.contextDir.name}</b> 吗?`,
        nzOkText: '删除',
        nzCancelText: '取消',
        nzOnOk: () => resolve(),
        nzOnCancel: () => reject()
      });
    })
      .then(() => new Promise((resolve, reject) => {
        Debugger
          .simulate<Result>({flag: true, data: false})
          .post(this.http, `/dir/del/${this.contextDir.id}`, {force: false})
          .subscribe(handleResult2({
            notify: this.notify,
            onOk: ({data}) => {
              // 删除成功, 结束调用链
              if (data) reject();
              // 删除失败, 询问是否强制删除?
              else resolve(data);
            },
            final: ({flag}) => !flag && reject()
          }));
      }))
      .then(emptyDir => {
        if (!emptyDir) {
          return new Promise((resolve, reject) => {
            this.modal.confirm({
              nzTitle: '警告',
              nzContent: `指定目录<b>${this.contextDir.name}</b>包含子目录或文件, 是否继续? <br><span class="red">[注意]:</span> 该操作不可逆`,
              nzOkText: '强制删除',
              nzCancelText: '取消',
              nzOnOk: () => resolve(),
              nzOnCancel: () => reject()
            });
          });
        }
      })
      .then(() => new Promise(resolve => {
        Debugger
          .simulate<Result>({flag: true})
          .post(this.http, `/dir/del/${this.contextDir.id}`, {force: true})
          .subscribe(handleResult2({
            notify: this.notify,
            final: () => resolve()
          }));
      }))
      .finally(() => {
        this.closeContextMenu();
        this.reloadDirsAndNotepads();
        this.checkOpenedNotepadExist();
      });

  }

  // 检查当前打开的文件是否还存在
  // 如果当前文件不存在需要把 editormd 中的内容重置
  private checkOpenedNotepadExist() {
    if (!this.openedNotepad || -1 == this.openedNotepad.id)
      return;

    this.isLoading = true;
    post(this.http, `/notepad/exist/${this.openedNotepad.id}`)
      .subscribe(handleResult2({
        notify: this.notify,
        onOk: ({data}) => !data && this.resetEditorMd(),
        final: () => this.isLoading = false
      }));
  }

  // 打开记事本右键菜单
  notepadContextMenu($event, menu, notepad: Notepad) {
    this.contextNotepad = notepad;
    this.showContextMenu = true;
    this.nzContextMenuService.create($event, menu);
  }

  // 记事本移动到其他目录
  notepadMoveToDir() {
    this.isLoading = true;
    this.chooseDirId = (this.contextNotepad.dir || Directory.ROOT).id;
    post(this.http, `/dir/all`, null)
      .subscribe(handleResult2({
        notify: this.notify,
        onOk: ({data}) => {
          this.notepadMovingModalVisible = true;
          let root = clone(Directory.ROOT);

          // 获取所有根节点
          data = data || [];
          let roots = data.filter(dir => !dir.parent);
          let nodeMap = {};
          eachA(data, dir => {
            nodeMap[dir.id] = dir;
            dir.title = dir.name;
            dir.key = dir.id;
          });

          // 过滤后剩余节点必定包含父节点
          data
            .filter(dir => !!dir.parent)
            .forEach((dir: Directory) => {
              let children = (nodeMap[dir.parent.id].children || []);
              children.push(dir);
              nodeMap[dir.parent.id].children = children;
            });

          // 标记叶子节点
          eachA(data, dir => {
            dir.isLeaf = !dir.children;
          });

          root.children = roots;
          root.expanded = true;
          root.title = '我的文件夹';
          root.key = Directory.ROOT.id;
          this.allDirs = [root];
        },
        final: () => this.isLoading = false
      }));
  }

  // 隐藏记事本"移动到"弹出框
  hideNotepadMoveModal() {
    this.notepadMovingModalVisible = false;
    this.closeContextMenu();
  }

  // 提交记事本"移动到"
  submitNotepadMoveModal() {
    this.notepadMovingModalVisible = false;
    this.isLoading = true;
    post(this.http, `/notepad/mv2Dir/${this.contextNotepad.id}/${this.chooseDirId}`)
      .subscribe(handleResult2({
        onOk: ({data}) => this.setParent(data),
        final: () => {
          this.isLoading = false;
          this.chooseDirId = Directory.ROOT.id;
          this.closeContextMenu();
        }
      }));
  }

  // 清理搜索关键字
  clearSearchKey() {
    this.searchKey = '';
    this.reloadDirsAndNotepads();
  }

  // 激活左侧菜单
  activeLeftNav(menu: LeftMenu) {
    if (menu.trail)
      this.activeLeftMenu = menu;

    if (isFunction(menu.click))
      menu.click();
  }

  // 获取最近记事本
  private latelyNotepad() {
    this.isLoading = true;
    this.dirs = [];
    this.notepads = [];

    Debugger
      .simulate<Result>({
        flag: true,
        data: <Notepad[]>[
          {
            id: 1,
            title: '文章1',
            content: '# 标题1',
            lastModified: new Date().getTime() - 5 * 1000,
            dir: new Directory(1, '目录1', '/目录1')
          },
          {
            id: 2,
            title: '文章2',
            content: '# 标题2',
            lastModified: new Date().getTime() - 60 * 1000,
            dir: new Directory(1, '目录1', '/目录1')
          },
          {
            id: 3,
            title: '文章3',
            content: '# 标题3',
            lastModified: new Date().getTime() - 60 * 60 * 1000,
            dir: new Directory(2, '目录2', '/目录2')
          },
          {
            id: 4,
            title: '文章4',
            content: '# 标题4',
            lastModified: new Date().getTime() - 24 * 60 * 60 * 1000,
            dir: new Directory(3, '目录3', '/目录3')
          },
          {
            id: 5, title: '文章5', content: '# 标题5',
            lastModified: new Date().getTime() - 11 * 24 * 60 * 60 * 1000,
          },
        ]
      })
      .post(this.http, `/notepad/lately`)
      .subscribe(handleResult2({
        notify: this.notify,
        onOk: ({data}) => this.setNotepads(data),
        final: () => this.isLoading = false
      }));
  }

  // 设置记事本列表
  private setNotepads(data: Array<Notepad>) {
    let now = new Date().getTime();
    this.notepads = eachA<Notepad>(data || [], (d: Notepad) => {
      d.lastModified = new Date(d.lastModified);
      let lm = d.lastModified.getTime();
      let diffMs = now - lm;

      // 一分钟内: x秒前
      const ms = 1000;
      const minuteMs = 60 * ms;
      if (minuteMs >= diffMs) {
        d.lastModified = Math.floor((diffMs + ms - 1) / ms) + '秒前';
        return;
      }

      // 一小时内: x分前
      const hourMs = 60 * minuteMs;
      if (hourMs >= diffMs) {
        d.lastModified = Math.floor((diffMs + minuteMs - 1) / minuteMs) + '分前';
        return;
      }

      // 一天内
      const dayMs = 24 * hourMs;
      if (dayMs >= diffMs) {
        d.lastModified = Math.floor((diffMs + hourMs - 1) / hourMs) + '小时前';
        return;
      }

      // 10天之内 10天前
      const day10 = dayMs * 10;
      if (day10 >= diffMs) {
        d.lastModified = Math.floor((diffMs + day10 - 1) / day10) + '天前';
        return;
      }

      // 其他 yyyy-MM
      d.lastModified = dateFmt(d.lastModified, 'yyyy-MM-dd');
    });
    if (!this.openedNotepad)
      this.openNotepad(this.notepads[0]);
  }

  // 删除记事本
  deleteNotepad() {
    if (isFunction(this.activeLeftMenu.deleteNotepad))
      this.activeLeftMenu.deleteNotepad(this.contextNotepad);
    else
      this.msgService.warning("不支持的操作");
  }

  // 获取回收站中的文章列表
  private listRecycles() {
    this.isLoading = true;
    this.dirs = [];
    this.notepads = [];
    Debugger
      .simulate<Result>({
        flag: true,
        data: <Recycle[]>[{
          id: 1,
          notepad: {id: 100, title: 'del-1', content: 'del-1-content', lastModified: new Date()}
        }]
      })
      .post(this.http, `/notepad/recycle`)
      .subscribe(handleResult2({
        notify: this.notify,
        onOk: ({data}) => this.setNotepads(extendPropsA(data, 'notepad')),
        final: () => {
          this.isLoading = false;
          this.closeContextMenu();
        }
      }));
  }

  // 记事本上下文菜单(更多)
  notepadContextMoreMenus() {
    return findA(['MINE_FOLDER', 'LATELY'], this.activeLeftMenu.type);
  }

  // 还原回收站
  restore() {
    this.isLoading = true;
    Debugger
      .simulate()
      .post(this.http, `/notepad/restore/${this.contextNotepad.id}`)
      .subscribe(handleResult2({
        notify: this.notify,
        showSuccess: true,
        onOk: () => this.listRecycles(),
        final: () => this.isLoading = false
      }));
  }
}
