package org.y.notepad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.y.notepad.model.entity.Directory;
import org.y.notepad.model.entity.Notepad;
import org.y.notepad.model.entity.User;
import org.y.notepad.model.enu.ErrorCode;
import org.y.notepad.model.enu.NotepadType;
import org.y.notepad.repository.NotepadRepository;
import org.y.notepad.service.DirectoryService;
import org.y.notepad.service.NotepadService;
import org.y.notepad.service.UserService;
import org.y.notepad.util.CollectionUtil;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class NotepadServiceImpl implements NotepadService {

    // private static final ThreadLocal<User> USER = new ThreadLocal<>();
    // private static final ThreadLocal<Directory> DIRECTORY = new ThreadLocal<>();

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
        notepad.setTitle("空白记事本");
        notepad.setContent("");
        notepad.setType(NotepadType.EMPTY);
        Date now = new Date();
        notepad.setCreateTime(now);
        notepad.setLastModified(now);
        notepad.setCreator(creator);
        notepad.setDir(dir);
        return notepadRepository.JPA.save(notepad);
    }

    @Override
    public List<Notepad> listByDir(int dirId, int userId) {
        User creator = userService.getById(userId);
        Directory dir = directoryService.getById(dirId);
        return notepadRepository.JPA.findAllByCreatorAndDir(creator, dir);
    }

    @Transactional
    @Override
    public void update(Notepad notepad) {
        int id = notepad.getId();
        Notepad old = notepadRepository.JPA.findById(id);
        if (null == old)
            ErrorCode.DENIED_OPERATION.breakOff("非法调用, 记事本数据无效!");

        User user = notepad.getCreator();
        if (!Objects.equals(user, old.getCreator()))
            ErrorCode.DENIED_OPERATION.breakOff("非法调用, 不允许修改他人数据!");

        old.setTitle(notepad.getTitle());
        String content = notepad.getContent();
        old.setContent(content);
        old.setLastModified(new Date());
        old.setSize(content.toCharArray().length);
        notepadRepository.JPA.save(old);
    }

    @Override
    public boolean checkExist(int userId, int id) {
        Notepad notepad = notepadRepository.JPA.findById(id);
        if (null == notepad)
            return false;

        if (notepad.getCreator().getId() != userId)
            ErrorCode.ILLEGAL_OPERATION.breakOff();

        return true;
    }
}
