package org.y.notepad.model.enu;

/**
 * 记事本状态
 */
public enum NotepadStatus {

    /**
     * 正常
     */
    NORMAL,

    /**
     * 回收站中
     */
    RECYCLE,

    /**
     * 已删除
     * TODO 可以把该状态的数据转移到归档数据库中
     */
    REMOVED

}
