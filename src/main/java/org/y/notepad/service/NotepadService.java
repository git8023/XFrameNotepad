package org.y.notepad.service;

import org.y.notepad.model.entity.Notepad;

import java.util.List;

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

    /**
     * 指定用户和目录获取记事本列表
     *
     * @param dirId  目录
     * @param userId 用户ID
     * @return 记事本列表
     */
    List<Notepad> listByDir(int dirId, int userId);

    /**
     * 更新记事本
     * @param notepad 记事本数据
     */
    void update(Notepad notepad);
}
