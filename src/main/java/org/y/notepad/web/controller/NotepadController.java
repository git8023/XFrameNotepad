package org.y.notepad.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y.notepad.model.entity.Directory;
import org.y.notepad.model.entity.Notepad;
import org.y.notepad.model.entity.User;
import org.y.notepad.model.result.Result;
import org.y.notepad.service.NotepadService;
import org.y.notepad.web.util.WebUtil;

import java.util.List;

/**
 * 文件控制器
 */
@RestController
@RequestMapping("/notepad")
public class NotepadController {

    private final NotepadService notepadService;

    @Autowired
    public NotepadController(NotepadService notepadService) {
        this.notepadService = notepadService;
    }

    /**
     * 新建空记事本
     *
     * @param dirId 目录ID
     */
    @RequestMapping("/newBlank/{dirId}")
    public Result newBlank(@PathVariable int dirId) {
        User user = WebUtil.getUser();
        int userId = user.getId();
        Notepad notepad = notepadService.createBlank(dirId, userId);
        return Result.data(notepad);
    }

    /**
     * 获取指定目录下所有记事本列表
     *
     * @param dirId 目录ID
     */
    @RequestMapping("/list/{dirId}")
    public Result list(@PathVariable int dirId) {
        User user = WebUtil.getUser();
        int userId = user.getId();
        List<Notepad> notepads = notepadService.listByDir(dirId, userId);
        return Result.data(notepads);
    }

    /**
     * 更新
     *
     * @param notepad 记事本ID
     */
    @RequestMapping("/update")
    public Result update(Notepad notepad) {
        User user = WebUtil.getUser();
        notepad.setCreator(user);
        notepadService.update(notepad);
        return Result.success();
    }

    /**
     * 检查指定记事本是否还存在
     *
     * @param id 记事本ID
     */
    @RequestMapping("/exist/{id}")
    public Result exist(@PathVariable int id) {
        User user = WebUtil.getUser();
        int userId = user.getUserId();
        boolean isExist = notepadService.checkExist(userId, id);
        return Result.data(isExist);
    }

    /**
     * 记事本移动到指定目录
     *
     * @param id    记事本ID
     * @param dirId 目标目录ID
     */
    @RequestMapping("/mv2Dir/{id}/{dirId}")
    public Result moveToDir(@PathVariable int id, @PathVariable int dirId) {
        User user = WebUtil.getUser();
        int userId = user.getId();
        Directory dir = notepadService.moveToDir(id, dirId, userId);
        return Result.data(dir);
    }

}
