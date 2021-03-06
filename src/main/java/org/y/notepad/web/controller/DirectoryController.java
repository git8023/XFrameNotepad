package org.y.notepad.web.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y.notepad.model.entity.Directory;
import org.y.notepad.model.entity.User;
import org.y.notepad.model.enu.ErrorCode;
import org.y.notepad.model.result.Result;
import org.y.notepad.service.DirectoryService;
import org.y.notepad.util.StringUtil;
import org.y.notepad.web.util.WebUtil;

import java.util.List;

/**
 * 目录请求控制器
 */
@RestController
@RequestMapping("/dir")
public class DirectoryController {

    private final DirectoryService directoryService;

    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    /**
     * 添加目录
     *
     * @param name     目录名
     * @param parentId 上级目录id
     */
    @RequestMapping("/add/{name}/{parentId}")
    public Result add(@PathVariable String name, @PathVariable String parentId) {
        User user = WebUtil.getUser();
        int userId = user.getId();

        // 添加根目录
        if (StringUtil.isBlank(parentId)) {
            directoryService.add(userId, name);
            return Result.success();
        }

        // 添加子目录
        try {
            int pid = Integer.parseInt(parentId);
            directoryService.add(userId, name, pid);
        } catch (NumberFormatException e) {
            ErrorCode.ILLEGAL_PARAMETER.breakOff("无效的上级目录参数");
        }
        return Result.success();
    }

    /**
     * 获取指定目录下的子目录
     *
     * @param id 目录ID
     */
    @RequestMapping("/dirs/{id}")
    public Result dirs(@PathVariable int id) {
        User user = WebUtil.getUser();
        int userId = user.getId();
        List<Directory> dirs = directoryService.getDirs(userId, id);
        return Result.data(dirs);
    }

    /**
     * 更新目录名称
     *
     * @param id   目录ID
     * @param name 新名称
     */
    @RequestMapping("/updateName")
    public Result updateName(int id, String name) {
        User user = WebUtil.getUser();
        int userId = user.getId();
        directoryService.updateName(id, name, userId);
        return Result.success();
    }

    /**
     * 删除目录, 空目录直接删除, 否则需要确认是否全部删除
     *
     * @param id    目录ID
     * @param force true-强制删除所有, false-只删除空目录
     */
    @RequestMapping("/del/{id}")
    public Result delete(@PathVariable int id, boolean force) {
        User user = WebUtil.getUser();
        int userId = user.getId();

        boolean ret = true;
        if (force) directoryService.deleteForce(id, userId);
        else ret = directoryService.delete(id, userId);

        return Result.data(ret);
    }

    /**
     * 获取当前用户的所有目录(排除根目录)
     */
    @RequestMapping("/all")
    public Result allDirs() {
        User user = WebUtil.getUser();
        int userId = user.getId();
        List<Directory> dirs = directoryService.getAllDirs(userId);
        return Result.data(dirs);
    }

    @RequestMapping("/detail/{id}")
    public Result detail(@PathVariable int id) {
        User user = WebUtil.getUser();
        int userId = user.getId();
        Directory dir = directoryService.detailById(userId, id);
        return Result.data(dir);
    }
}
