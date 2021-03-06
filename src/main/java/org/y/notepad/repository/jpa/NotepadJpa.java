package org.y.notepad.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.y.notepad.model.entity.Directory;
import org.y.notepad.model.entity.Notepad;
import org.y.notepad.model.entity.User;
import org.y.notepad.model.enu.NotepadType;

import java.util.List;

public interface NotepadJpa extends JpaRepository<Notepad, Integer> {

    /**
     * 指定创建者和类型获取记事本列表
     *
     * @param creator 创建者
     * @param type    类型
     * @param dir     所在目录
     * @return 记事本列表
     */
    List<Notepad> findAllByCreatorAndTypeAndDir(User creator, NotepadType type, Directory dir);

    /**
     * 指定创建者和目录查询记事本列表
     *
     * @param creator 创建者
     * @param dir     目录
     * @return 记事本列表
     */
    List<Notepad> findAllByCreatorAndDir(User creator, Directory dir);

    /**
     * 指定ID获取记事本信息
     *
     * @param id 记事本ID
     * @return 记事本信息
     */
    Notepad findById(int id);
}
