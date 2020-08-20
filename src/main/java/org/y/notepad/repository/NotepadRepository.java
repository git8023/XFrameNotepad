package org.y.notepad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.y.notepad.repository.jpa.NotepadJpa;
import org.y.notepad.repository.mapper.NotepadMapper;

@Component
public class NotepadRepository {

    public final NotepadJpa JPA;
    public final NotepadMapper MAPPER;

    @Autowired
    public NotepadRepository(
            NotepadJpa JPA,
            NotepadMapper MAPPER
    ) {
        this.JPA = JPA;
        this.MAPPER = MAPPER;
    }
}
