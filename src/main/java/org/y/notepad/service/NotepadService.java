package org.y.notepad.service;

import org.y.notepad.model.entity.Directory;
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
     *
     * @param notepad 记事本数据
     */
    void update(Notepad notepad);

    /**
     * 检查指定记事本是否存在
     *
     * @param userId 用户ID
     * @param id     记事本ID
     * @return true-存在, false-不存在
     */
    boolean checkExist(int userId, int id);

    /**
     * 记事本移动到指定目录
     *
     * @param id     记事本ID
     * @param dirId  目标目录ID
     * @param userId 当前用户ID
     * @return 目标目录, 如果{@code -1 == dirId}总是返回null
     */
    Directory moveToDir(int id, int dirId, int userId);

    /**
     * 获取最近修改的记事本
     *
     * @param userId 当前用户ID
     * @return 记事本列表
     */
    List<Notepad> lately(int userId);

    /**
     * 删除指定记事本
     *
     * @param userId  当前用户ID
     * @param id      记事本ID
     * @param recycle true-操作回收站, false-操作原数据
     */
    void deleteById(int userId, int id, boolean recycle);
}
