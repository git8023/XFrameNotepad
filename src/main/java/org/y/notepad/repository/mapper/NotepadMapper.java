package org.y.notepad.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.springframework.stereotype.Repository;

@Repository
public interface NotepadMapper {

    /**
     * 删除指定目录下所有文件
     *
     * @param dirId 目录ID
     * @return 数据库受影响行数
     */
    @Delete({
            ""
    })
    int deleteAllByDir(int dirId);

}
