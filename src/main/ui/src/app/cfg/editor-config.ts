/**
 * 初始化剪贴板/拖拽文件上传
 * @param Editor 编辑器实例
 */
export function initPasteDragImg(Editor) {
  let doc = document.getElementById(Editor.id)
  doc.addEventListener('paste', function (event) {
    // @ts-ignore
    let items = (event.clipboardData || window.clipboardData).items;
    let file = null;
    if (items && items.length) {
      // 搜索剪切板items
      for (let i = 0; i < items.length; i++) {
        if (items[i].type.indexOf('image') !== -1) {
          file = items[i].getAsFile();
          break;
        }
      }
    } else {
      console.log("当前浏览器不支持");
      return;
    }
    if (!file) {
      console.log("粘贴内容非图片");
      return;
    }
    uploadImg(file, Editor, true);
  });
  let dashboard = document.getElementById(Editor.id)
  dashboard.addEventListener("dragover", function (e) {
    e.preventDefault()
    e.stopPropagation()
  })
  dashboard.addEventListener("dragenter", function (e) {
    e.preventDefault()
    e.stopPropagation()
  })
  dashboard.addEventListener("drop", function (e) {
    e.preventDefault()
    e.stopPropagation()
    // @ts-ignore
    let files = this.files || e.dataTransfer.files;
    uploadImg(files[0], Editor);
  })
}

function uploadImg(file, Editor, isImg = false) {
  let formData = new FormData();
  let fileName = new Date().getTime() + "." + file.name.split(".").pop();
  formData.append('editormd-image-file', file, fileName);
  // @ts-ignore
  $.ajax({
    url: Editor.settings.imageUploadURL,
    type: 'post',
    data: formData,
    processData: false,
    contentType: false,
    dataType: 'json',
    success: function (msg) {
      let success = msg['success'];
      if (success == 1) {
        let url = msg["url"];
        if (isImg || /\.(png|jpg|jpeg|gif|bmp|ico)$/.test(url)) {
          Editor.insertValue("![图片alt](" + msg["url"] + " ''图片title'')");
        } else {
          Editor.insertValue("[下载附件](" + msg["url"] + ")");
        }
      } else {
        console.log(msg);
        alert("上传失败");
      }
    }
  });
}

/**
 * Editor.ms 配置
 */
export class EditorConfig {
  public width = '100%';
  public height = '100%';
  public path = './assets/editor.md/lib/';
  public codeFold: true;
  public searchReplace = true;
  public toolbar = true;
  public emoji = true;
  public taskList = false;
  public tex = false;// 数学公式类默认不解析
  public readOnly = false;
  public tocm = true;
  public watch = true;
  public previewCodeHighlight = true;
  public saveHTMLToTextarea = true;
  public markdown = '';
  public flowChart = false;//流程图
  public syncScrolling = true;
  public sequenceDiagram = false;//UML时序图
  public imageUpload = true;
  public imageFormats = ['jpg', 'jpeg', 'gif', 'png', 'bmp', 'webp'];
  public imageUploadURL = '/upload/up';
  public htmlDecode = 'style,script,iframe';  // you can filter tags decode
  public editorFunction = '';//定义调用的方法
  public onchange?: () => void;
  public onload?: () => void;

  constructor() {
  }
}
