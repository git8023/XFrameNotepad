package org.y.notepad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y.notepad.model.entity.Directory;
import org.y.notepad.model.entity.User;

import java.util.List;

@Repository
public interface DirectoryJpa extends JpaRepository<Directory, Integer> {

    /**
     * 指定名称查询目录列表
     *
     * @param name 目录名称
     * @return 目录列表
     */
    List<Directory> findAllByName(String name);

    /**
     * 指定ID获取目录信息
     *
     * @param id ID
     * @return 目录信息
     */
    Directory findById(int id);

    /**
     * 指定创建者获取所有目录
     *
     * @param creator 创建者
     * @return 目录列表
     */
    List<Directory> findAllByCreator(User creator);

}
