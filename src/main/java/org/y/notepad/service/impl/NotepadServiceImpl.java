package org.y.notepad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.y.notepad.model.entity.Directory;
import org.y.notepad.model.entity.Notepad;
import org.y.notepad.model.entity.Recycle;
import org.y.notepad.model.entity.User;
import org.y.notepad.model.enu.ErrorCode;
import org.y.notepad.model.enu.NotepadStatus;
import org.y.notepad.model.enu.NotepadType;
import org.y.notepad.repository.DirectoryRepository;
import org.y.notepad.repository.RecycleRepository;
import org.y.notepad.repository.NotepadRepository;
import org.y.notepad.service.NotepadService;
import org.y.notepad.service.UserService;
import org.y.notepad.util.CollectionUtil;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class NotepadServiceImpl implements NotepadService {
    @Value("${notepad.lately-count}")
    private int NOTEPAD_LATELY_COUNT;

    private final NotepadRepository notepadRepository;
    private final UserService userService;
    private final DirectoryRepository directoryRepository;
    private final RecycleRepository recycleRepository;

    @Autowired
    public NotepadServiceImpl(
            NotepadRepository notepadRepository,
            UserService userService,
            DirectoryRepository directoryRepository,
            RecycleRepository recycleRepository
    ) {
        this.notepadRepository = notepadRepository;
        this.userService = userService;
        this.directoryRepository = directoryRepository;
        this.recycleRepository = recycleRepository;
    }

    @Transactional
    @Override
    public Notepad createBlank(int dirId, int userId) {
        User creator = userService.getById(userId);
        Directory dir = directoryRepository.JPA.findById(dirId);

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
        Directory dir = directoryRepository.JPA.findById(dirId);
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
        old.setType(NotepadType.MARKDOWN);
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

    @Override
    public Directory moveToDir(int id, int dirId, int userId) {
        Notepad notepad = notepadRepository.JPA.findById(id);
        if (notepad.getCreator().getId() != userId)
            ErrorCode.ILLEGAL_OPERATION.breakOff();

        // 记事本在根目录下
        // 并且目标目录也是根目录
        Directory dir = notepad.getDir();
        if (null == dir && -1 == dirId)
            return null;

        // 记事本移动到目标目录
        // 如果目标目录为根目录, notepad指定为null目录
        Directory targetDir = directoryRepository.JPA.findById(dirId);
        notepad.setDir(targetDir);
        notepadRepository.JPA.save(notepad);

        // 如果目标目录有效
        // 需要把目标目录的创建者信息清空后再返回给客户端
        if (null != targetDir)
            targetDir.setCreator(null);

        return targetDir;
    }

    @Override
    public List<Notepad> lately(int userId) {
        return notepadRepository.MAPPER.selectListByLastModifiedDescAndLimit(userId, NOTEPAD_LATELY_COUNT);
    }

    @Transactional
    @Override
    public void deleteById(int userId, int id) {
        Notepad notepad = notepadRepository.JPA.findById(id);
        if (null == notepad)
            return;

        if (notepad.getCreator().getId() != userId)
            ErrorCode.ILLEGAL_OPERATION.breakOff();

        notepad.setStatus(NotepadStatus.RECYCLE);
        notepadRepository.JPA.save(notepad);
        recycleRepository.JPA.save(new Recycle(notepad));
    }
}
