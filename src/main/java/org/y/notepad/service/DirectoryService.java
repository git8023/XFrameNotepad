package org.y.notepad.service;

import org.y.notepad.model.entity.Directory;
import org.y.notepad.model.entity.User;

import java.util.List;

/**
 * 目录服务接口
 */
public interface DirectoryService {

    /**
     * 添加根目录
     *
     * @param userId   用户ID
     * @param name     目录名
     * @param parentId 上级目录ID
     */
    void add(int userId, String name, int parentId);

    /**
     * 添加子目录
     *
     * @param userId 用户ID
     * @param name   目录名
     */
    void add(int userId, String name);

    /**
     * 获取子目录列表
     *
     * @param userId   用户ID
     * @param parentId 父级目录ID
     * @return 子目录列表
     */
    List<Directory> getDirs(int userId, int parentId);

    /**
     * 指定ID获取目录
     *
     * @param id 目录ID
     * @return 目录信息
     */
    Directory getById(int id);
}
