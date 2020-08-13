import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {EditorConfig} from "../../cfg/editor-config";

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

  constructor() {
  }

  ngOnInit(): void {
    this.conf.markdown = "测试代码 <b>粗体</b>";
    this.conf.onchange = () => {
      console.log(this.editor.getMarkdown());
    };
    this.editor = editormd('mdEditor', this.conf);
    console.log(this.editor);
    // 获取 md 内容
    // editor.getMarkdown()
    // 解析 md 内容
    // editormd.markdownToHTML('detailmarkdown', this.conf);
  }

  // 退出应用
  exitApp() {
    top.postMessage(JSON.stringify('WIN_EXIT'), '*');
  }

}
