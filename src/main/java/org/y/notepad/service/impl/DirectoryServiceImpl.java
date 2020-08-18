package org.y.notepad.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.y.notepad.model.entity.Directory;
import org.y.notepad.model.entity.User;
import org.y.notepad.model.enu.ErrorCode;
import org.y.notepad.repository.DirectoryRepository;
import org.y.notepad.repository.UserRepository;
import org.y.notepad.service.DirectoryService;

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

    public DirectoryServiceImpl(DirectoryRepository directoryRepository, UserRepository userRepository) {
        this.directoryRepository = directoryRepository;
        this.userRepository = userRepository;
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

    /**
     * 转换为完整路径
     *
     * @param dir 目录
     * @return 完整路径
     */
    private String getFullPath(Directory dir) {
        Directory parent = dir.getParent();
        String parentPath = null != parent ? parent.getPath() : "";
        return "/" + dir.getName() + parentPath;

    }
}
