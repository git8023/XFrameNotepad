package org.y.notepad.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.y.notepad.model.entity.Recycle;

import java.util.List;

@Repository
public interface RecycleMapper {

    /**
     * 查询所有回收站列表数据
     *
     * @param userId 用户ID
     * @return 回收列表
     */
    @Select({
            "SELECT",
            "    notepad.id                 `notepad.id`,",
            "    notepad.content            `notepad.content`,",
            "    notepad.create_time        `notepad.createTime`,",
            "    notepad.last_modified      `notepad.lastModified`,",
            "    notepad.size               `notepad.size`,",
            "    notepad.title              `notepad.title`,",
            "    notepad.type               `notepad.type`,",
            "    notepad.user_id            `notepad.creator.id`,",
            "    notepad.dir_id             `notepad.dir.id`,",
            "    notepad.`status`           `notepad.status`,",
            "    recycle.id                 `id`,",
            "    recycle.create_time        `createTime`",
            "FROM",
            "    recycle",
            "INNER JOIN ",
            "    notepad ON notepad.id = recycle.notepad_id",
            "WHERE",
            "    notepad.user_id = #{userId}"
    })
    List<Recycle> selectAll(int userId);

}
