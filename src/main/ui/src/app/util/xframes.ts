import {eachA} from "./utils";

/**
 * XFrame通信工具
 */
export class XFrames {

  /**
   * 向顶层窗口发送数据
   * @param t 类型
   * @param data 数据, 数据以JSON格式发送
   */
  static emitType(t: EmitType, data?: any): void {
    let msg = JSON.stringify({type: t, data: data});
    top.postMessage(msg, '*');
  }

  /**
   * 按事件类型处理
   */
  static onType(): EventHandlerStore {
    return new EventHandlerStore();
  }
}

/**
 * 事件处理回调接口
 */
export interface EventHandler {

  /**
   * @param data 被处理数据
   * @return 如果事件未处理完成需要返回false, 否则会继续调用相同类型的其他处理函数
   */
  (data: any): (void | boolean | Promise<boolean>)
}

/**
 * 事件对象
 */
export class EventData {
  /**
   * 事件类型
   */
  type: EmitType;

  /**
   * 事件数据
   */
  data: any;

  /**
   * 其他数据
   */
  [s: string]: any;
}

/**
 * 事件处理器
 */
export class EventHandlerStore {

  /**
   * 处理函数仓库
   * K - 事件类型
   * V - 事件处理器列表
   */
  private static store: { [n: number]: Array<EventHandler> } = {};

  /**
   * 监听状态
   * true-已经开始监听
   * false-还未开始
   */
  private static isListening: boolean = false;

  constructor() {
    EventHandlerStore.listening();
  }

  /**
   * 检查指定类型并进行处理
   * @param type 可处理类型
   * @param callback 处理函数, 按调用顺序依次执行
   */
  loopup(type: EmitType, callback: EventHandler): EventHandlerStore {
    let ths = EventHandlerStore.store[type] || [];
    ths.push(callback);
    EventHandlerStore.store[type] = ths;
    return this;
  }

  /**
   * 开始监听消息
   */
  private static listening() {
    if (!this.isListening) {
      this.isListening = true;
      window.addEventListener("message", evt => {
        let data: EventData = JSON.parse(evt.data);
        this.handle(data);
      });
    }
  }

  /**
   * 调用处理函数
   * @param ev 事件对象
   */
  private static handle(ev: EventData) {
    let handlers = this.store[ev.type] || [];
    eachA(handlers, f => false == f(ev.value));
  }

}


/**
 * 事件类型
 */
export enum EmitType {
  /**
   * 退出
   */
  EXIT,

  /**
   * 副标题
   */
  SUBHEAD
}
