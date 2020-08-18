package org.y.notepad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.y.notepad.model.entity.Directory;
import org.y.notepad.model.entity.Notepad;
import org.y.notepad.model.entity.User;
import org.y.notepad.model.enu.NotepadType;
import org.y.notepad.repository.NotepadRepository;
import org.y.notepad.service.DirectoryService;
import org.y.notepad.service.NotepadService;
import org.y.notepad.service.UserService;
import org.y.notepad.util.CollectionUtil;

import java.util.Date;
import java.util.List;

@Service
public class NotepadServiceImpl implements NotepadService {

    private final NotepadRepository notepadRepository;
    private final UserService userService;
    private final DirectoryService directoryService;

    @Autowired
    public NotepadServiceImpl(
            NotepadRepository notepadRepository,
            UserService userService,
            DirectoryService directoryService
    ) {
        this.notepadRepository = notepadRepository;
        this.userService = userService;
        this.directoryService = directoryService;
    }

    @Transactional
    @Override
    public Notepad createBlank(int dirId, int userId) {
        User creator = userService.getById(userId);
        Directory dir = directoryService.getById(dirId);

        // 已经存在了空记事本
        List<Notepad> notepads = notepadRepository.JPA.findAllByCreatorAndTypeAndDir(creator, NotepadType.EMPTY, dir);
        if (CollectionUtil.isNotEmpty(notepads))
            return notepads.get(0);

        Notepad notepad = new Notepad();
        notepad.setTitle("");
        notepad.setContent("");
        notepad.setType(NotepadType.EMPTY);
        notepad.setCreateTime(new Date());
        notepad.setCreator(creator);
        notepad.setDir(dir);
        return notepadRepository.JPA.save(notepad);
    }
}
