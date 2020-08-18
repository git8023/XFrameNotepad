package org.y.notepad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.y.notepad.repository.jpa.NotepadJpa;

@Component
public class NotepadRepository {

    public final NotepadJpa JPA;

    @Autowired
    public NotepadRepository(NotepadJpa JPA) {
        this.JPA = JPA;
    }
}
