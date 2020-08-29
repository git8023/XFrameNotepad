package org.y.notepad.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.y.notepad.model.enu.NotepadStatus;
import org.y.notepad.model.enu.NotepadType;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(of = "id")
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Notepad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * 标题(文件名)
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建者
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    /**
     * 所在文件夹
     */
    @ManyToOne
    @JoinColumn(name = "dir_id")
    private Directory dir;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后修改时间
     */
    private Date lastModified;

    /**
     * 内容占用字节数
     */
    private long size;

    /**
     * 类型
     */
    private NotepadType type;

    /**
     * 状态
     */
    private NotepadStatus status;
}
