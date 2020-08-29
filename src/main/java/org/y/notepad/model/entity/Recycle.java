package org.y.notepad.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table
@NoArgsConstructor
public class Recycle {

    /**
     * 自增ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 记事本
     */
    @OneToOne
    @JoinColumn
    private Notepad notepad;

    /**
     * 目录
     */
    @OneToOne
    @JoinColumn
    private Directory directory;

    /**
     * 创建时间
     */
    private Date createTime;

    public Recycle(Notepad notepad) {
        this.notepad = notepad;
        this.createTime = new Date();
    }

    public Recycle(Directory dir) {
        this.directory = dir;
        this.createTime = new Date();
    }
}
