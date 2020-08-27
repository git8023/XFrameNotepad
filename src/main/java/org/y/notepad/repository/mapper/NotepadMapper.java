package org.y.notepad.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.y.notepad.model.entity.Notepad;

import java.util.List;

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

    /**
     * 按指定关键字前缀模糊匹配记事本列表
     *
     * @param userId        用户ID
     * @param namePrefixKey 名称前缀
     * @return 记事本列表
     */
    @Select({
            "<script>",
            "SELECT",
            "    notepad.id,",
            "    notepad.title,",
            "    notepad.dir_id",
            "FROM",
            "    notepad",
            "WHERE",
            "    notepad.user_id = #{userId}",
            "    <if test='null != key'>",
            "        AND notepad.title LIKE #{key}",
            "    </if>",
            "</script>",
    })
    List<Notepad> selectListByNameKey(
            @Param("userId") int userId,
            @Param("key") String namePrefixKey
    );

}
