package org.y.notepad.repository.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.y.notepad.model.entity.Directory;

import java.util.List;

@Repository
public interface DirectoryMapper {

    /**
     * 指定用户和父目录查询子目录列表
     *
     * @param userId   用户ID
     * @param parentId 父目录ID
     * @return 子目录列表
     */
    @Select({
            "<script>",
            "SELECT",
            "    `directory`.id             `id`,",
            "    `directory`.create_time    `createTime`,",
            "    `directory`.`name`         `name`,",
            "    `directory`.path           `path`,",
            "    `directory`.user_id        `creator.id`,",
            "    `directory`.parent_id      `parent.id`",
            "FROM",
            "    `directory`",
            "WHERE",
            "    `directory`.user_id = #{userId}",
            "    AND (",
            "        <choose>",
            "            <when test='null != parentId'> `directory`.parent_id = #{parentId}</when>",
            "            <otherwise>`directory`.parent_id IS NULL</otherwise>",
            "        </choose>",
            "    )",
            "</script>",
    })
    List<Directory> selectListByCreatorAndParent(
            @Param("userId") int userId,
            @Param("parentId") Integer parentId);

    /**
     * 指定路径(前缀)删除目录(子目录)
     *
     * @param userId 用户ID
     * @param path   路径
     * @return 数据库受影响行数
     */
    @Delete({
            "DELETE FROM ",
            "    `directory`",
            "WHERE ",
            "    `directory`.user_id = #{userId}",
            "    AND (",
            "        `directory`.path = #{path}",
            "        OR `directory`.path LIKE CONCAT(#{path}, '/%')",
            "    )"
    })
    int deleteAllByPath(
            @Param("userId") int userId,
            @Param("path") String path);

    /**
     * 按指定关键字搜索目录
     *
     * @param userId 用户ID
     * @param key    关键字, e.g: {key}%
     * @return 目录列表
     */
    @Select({
            "<script>",
            "SELECT",
            "    `directory`.id             `id`,",
            "    `directory`.create_time    `createTime`,",
            "    `directory`.`name`         `name`,",
            "    `directory`.path           `path`,",
            "    `directory`.user_id        `creator.id`,",
            "    `directory`.parent_id      `parent.id`",
            "FROM",
            "    `directory`",
            "WHERE",
            "    `directory`.user_id = #{userId}",
            "    <if test='null != key'>",
            "        AND `directory`.`name` LIKE #{key}",
            "    </if>",
            "</script>",
    })
    List<Directory> selectListByPathKey(
            @Param("userId") int userId,
            @Param("key") String key
    );

}
