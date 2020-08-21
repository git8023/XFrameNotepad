package org.y.notepad.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotepadMapper {

    /**
     * 删除指定目录下所有文件
     *
     * @param userId     用户ID
     * @param pathPrefix 路径前缀匹配, e.g: /dirPath
     * @return 数据库受影响行数
     */
    @Delete({
            "DELETE FROM ",
            "    notepad",
            "WHERE",
            "    notepad.user_id = #{userId}",
            "    AND notepad.dir_id IN (",
            "        SELECT id",
            "        FROM `directory`",
            "        WHERE `directory`.path = #{pathPrefix} OR `directory`.path LIKE CONCAT(#{pathPrefix}, '/%')",
            "    )"
    })
    int deleteAllByParentPath(
            @Param("userId") int userId,
            @Param("pathPrefix") String pathPrefix);

}
