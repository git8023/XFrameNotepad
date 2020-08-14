package org.y.notepad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.y.notepad.repository.jpa.UserJpa;

@Component
public class UserRepository {
    public final UserJpa JPA;

    @Autowired
    public UserRepository(UserJpa JPA) {
        this.JPA = JPA;
    }
}
