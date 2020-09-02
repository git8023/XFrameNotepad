package org.y.notepad.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * 目录数据结构
 */
@Data
@Entity
@Table
@NoArgsConstructor
public class Directory {

    @Id
    @GeneratedValue
    private int id;

    /**
     * 当前目录名
     */
    private String name;

    /**
     * 唯一父级目录, 如果没有可以为null
     */
    @OneToOne
    @JoinColumn(name = "parent_id")
    private Directory parent;

    /**
     * 完整路径
     */
    private String path;

    /**
     * 目录所属创建者
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后修改时间
     */
    private Date lastModified;

    public Directory(int id) {
        this.id = id;
    }
}
