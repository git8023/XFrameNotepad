package org.y.notepad.repository;

import org.springframework.stereotype.Component;
import org.y.notepad.repository.jpa.RecycleJpa;
import org.y.notepad.repository.mapper.RecycleMapper;

@Component
public class RecycleRepository {

    public final RecycleJpa JPA;
    public final RecycleMapper MAPPER;

    public RecycleRepository(RecycleJpa JPA, RecycleMapper MAPPER) {
        this.JPA = JPA;
        this.MAPPER = MAPPER;
    }
}
