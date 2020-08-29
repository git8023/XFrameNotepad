package org.y.notepad.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user")
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    /**
     * 当前应用中的用户自增ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Core中的用户ID
     */
    private int userId;

    /**
     * 授权码
     */
    private String token;

    /**
     * 创建时间
     */
    private Date createDate;

    public User(int userId) {
        this.userId = userId;
    }
}
