package org.y.notepad.service;

import org.y.notepad.model.entity.Notepad;

/**
 * 文件服务接口
 */
public interface NotepadService {

    /**
     * 在指定目录创建空记事本, 同一目录下只允许存在一个空记事本
     *
     * @param dirId  目录
     * @param userId 用户ID
     * @return 空记事本数据
     */
    Notepad createBlank(int dirId, int userId);

}
