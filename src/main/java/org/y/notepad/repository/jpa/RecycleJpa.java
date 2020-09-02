package org.y.notepad.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y.notepad.model.entity.Notepad;
import org.y.notepad.model.entity.Recycle;

@Repository
public interface RecycleJpa extends JpaRepository<Recycle, Integer> {

    /**
     * 指定记事本删除数据
     *
     * @param notepad 记事本
     */
    void deleteByNotepad(Notepad notepad);

    /**
     * 指定记事本查询回收站信息
     *
     * @param notepad 记事本
     * @return 回收站信息
     */
    Recycle findByNotepad(Notepad notepad);
}

