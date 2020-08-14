import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {EditorConfig} from "../../cfg/editor-config";
import {catchErr, handleResult2, post, Storages} from '../../util/utils';
import {HttpClient} from '@angular/common/http';
import {NzModalService, NzNotificationService} from 'ng-zorro-antd';

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

  constructor(
    private http: HttpClient,
    private notify: NzNotificationService,
    private modal: NzModalService
  ) {
  }

  ngOnInit(): void {
    let userId = Storages.SESSION.user();
    if (!userId) {
      this.modal.confirm({
        nzTitle: '警告',
        nzContent: '非法调用',
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
    top.postMessage(JSON.stringify('WIN_EXIT'), '*');
  }

  // 新建文件夹
  addDir() {
    post(this.http, `/dir/add`, null, ...catchErr())
      .subscribe(handleResult2({
        notify: this.notify,
        onOk: () => console.log('创建成功')
      }));
  }

  // 登录
  private login() {
    let userId = Storages.SESSION.user();
    post(this.http, `/user/login`, {userId}, ...catchErr())
      .subscribe(handleResult2({
        notify: this.notify,
        onOk: ({data})=> {
          Storages.SESSION.data(data);
          this.init();
        },
        onFail: this.exitApp
      }));
  }

  // 初始化
  private init() {

    this.conf.markdown = "测试代码 <b>粗体</b>";
    this.conf.onchange = () => {
      console.log(this.editor.getMarkdown());
    };
    this.editor = editormd('mdEditor', this.conf);

  }
}
