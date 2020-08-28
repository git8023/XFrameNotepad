package org.y.notepad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.y.notepad.model.entity.Directory;
import org.y.notepad.model.entity.Notepad;
import org.y.notepad.model.entity.User;
import org.y.notepad.model.enu.ErrorCode;
import org.y.notepad.repository.DirectoryRepository;
import org.y.notepad.repository.NotepadRepository;
import org.y.notepad.repository.UserRepository;
import org.y.notepad.service.DirectoryService;
import org.y.notepad.util.CollectionUtil;
import org.y.notepad.util.StringUtil;

import java.util.Date;
import java.util.List;

@Service
public class DirectoryServiceImpl implements DirectoryService {

    /**
     * 根目录父级ID
     */
    private static final int ROOT_DIRECTORY_ID = -1;

    private final DirectoryRepository directoryRepository;
    private final UserRepository userRepository;
    private final NotepadRepository notepadRepository;

    @Autowired
    public DirectoryServiceImpl(
            DirectoryRepository directoryRepository,
            UserRepository userRepository,
            NotepadRepository notepadRepository) {
        this.directoryRepository = directoryRepository;
        this.userRepository = userRepository;
        this.notepadRepository = notepadRepository;
    }

    @Transactional
    @Override
    public void add(int userId, String name, int parentId) {

        // 授权检查
        User user = userRepository.JPA.findById(userId);
        if (null == user)
            ErrorCode.ILLEGAL_PARAMETER.breakOff("非法调用, 用户未授权");

        // 检查同级目录下是否存在相同目录名
        List<Directory> dirs = directoryRepository.JPA.findAllByName(name);
        dirs.forEach(dir -> {
            Directory parent = dir.getParent();
            if (parent.getId() == parentId)
                ErrorCode.DENIED_OPERATION.breakOff("当前目录下已存在同名目录");
        });

        // 获取父级目录
        boolean isRoot = (ROOT_DIRECTORY_ID == parentId);
        Directory parent = directoryRepository.JPA.findById(parentId);
        if (null == parent && !isRoot)
            ErrorCode.ILLEGAL_PARAMETER.breakOff("非法调用, 父级目录无效.");

        // 添加目录
        Directory dir = new Directory();
        dir.setCreator(user);
        dir.setName(name);
        dir.setParent(parent);
        dir.setPath(getFullPath(dir));
        dir.setCreateTime(new Date());
        directoryRepository.JPA.save(dir);
    }

    @Transactional
    @Override
    public void add(int userId, String name) {
        add(userId, name, ROOT_DIRECTORY_ID);
    }

    @Override
    public List<Directory> getDirs(int userId, int parentId) {
        Integer pid = (ROOT_DIRECTORY_ID == parentId) ? null : parentId;
        return directoryRepository.MAPPER.selectListByCreatorAndParent(userId, pid);
    }

    @Override
    public Directory getById(int id) {
        return directoryRepository.JPA.findById(id);
    }

    @Override
    public void updateName(int id, String newlyName, int userId) {
        Directory dir = directoryRepository.JPA.findById(id);
        if (dir.getCreator().getId() != userId)
            ErrorCode.ILLEGAL_OPERATION.breakOff();

        if (!StringUtil.equalsTwo(newlyName, dir.getName())) {
            dir.setName(newlyName);
            dir.setLastModified(new Date());
            directoryRepository.JPA.save(dir);
        }
    }

    @Override
    public boolean delete(int id, int userId) {
        Directory dir = directoryRepository.JPA.findById(id);
        if (dir.getCreator().getId() != userId)
            ErrorCode.ILLEGAL_OPERATION.breakOff();

        List<Directory> dirs = getDirs(userId, id);
        if (CollectionUtil.isNotEmpty(dirs))
            return false;

        User creator = userRepository.JPA.findById(userId);
        List<Notepad> notepads = notepadRepository.JPA.findAllByCreatorAndDir(creator, dir);
        if (CollectionUtil.isNotEmpty(notepads))
            return false;

        directoryRepository.JPA.delete(dir);
        return true;
    }

    @Transactional
    @Override
    public void deleteForce(int id, int userId) {
        Directory dir = directoryRepository.JPA.findById(id);
        if (dir.getCreator().getId() != userId)
            ErrorCode.ILLEGAL_OPERATION.breakOff();

        // 删除所有文件
        String path = dir.getPath();
        notepadRepository.MAPPER.deleteAllByParentPath(userId, path);

        // 删除所有目录
        directoryRepository.MAPPER.deleteAllByPath(userId, path);
    }

    @Override
    public List<Directory> getAllDirs(int userId) {
        User user = userRepository.JPA.findById(userId);
        List<Directory> list = directoryRepository.JPA.findAllByCreator(user);
        list.forEach(dir -> {
            dir.setCreator(null);
            Directory parent = dir.getParent();
            if (null != parent) {
                dir.setParent(new Directory(parent.getId()));
            }
        });
        return list;
    }

    @Override
    public Directory detailById(int userId, int id) {
        Directory dir = directoryRepository.JPA.findById(id);
        if (null == dir)
            return null;

        // 父级目录中可能有 User.token
        Directory parent = dir.getParent();
        if (null != parent)
            parent.setParent(null);

        User creator = dir.getCreator();
        if (creator.getId() != userId)
            ErrorCode.DENIED_OPERATION.breakOff();
        creator.setToken(null);
        return dir;
    }

    /**
     * 转换为完整路径
     *
     * @param dir 目录
     * @return 完整路径
     */
    private String getFullPath(Directory dir) {
        Directory parent = dir.getParent();
        String parentPath = null != parent ? parent.getPath() : "";
        return parentPath + "/" + dir.getName();

    }
}
