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

  conf = new EditorConfig();

  constructor() {
  }

  ngOnInit(): void {
    this.conf.markdown = "测试代码 <b>粗体</b>";
    editormd('detailmarkdown', this.conf);
  }
}
