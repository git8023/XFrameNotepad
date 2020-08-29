package org.y.notepad.repository;

import org.springframework.stereotype.Component;
import org.y.notepad.repository.jpa.RecycleJpa;

@Component
public class RecycleRepository {

    public final RecycleJpa JPA;

    public RecycleRepository(RecycleJpa JPA) {
        this.JPA = JPA;
    }
}
