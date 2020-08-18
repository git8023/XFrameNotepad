package org.y.notepad.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y.notepad.model.entity.Notepad;
import org.y.notepad.model.entity.User;
import org.y.notepad.model.result.Result;
import org.y.notepad.service.NotepadService;
import org.y.notepad.web.util.WebUtil;

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

}
