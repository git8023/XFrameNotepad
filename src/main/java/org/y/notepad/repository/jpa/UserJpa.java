package org.y.notepad.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y.notepad.model.entity.User;

@Repository
public interface UserJpa extends JpaRepository<User, Integer> {
}
