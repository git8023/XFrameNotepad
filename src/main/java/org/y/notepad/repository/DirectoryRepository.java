package org.y.notepad.repository;

import org.springframework.stereotype.Component;
import org.y.notepad.repository.mapper.DirectoryMapper;

@Component
public class DirectoryRepository {

    public final DirectoryJpa JPA;
    public final DirectoryMapper MAPPER;

    public DirectoryRepository(DirectoryJpa JPA, DirectoryMapper MAPPER) {
        this.JPA = JPA;
        this.MAPPER = MAPPER;
    }
}
