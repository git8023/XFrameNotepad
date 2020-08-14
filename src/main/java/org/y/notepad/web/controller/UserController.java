package org.y.notepad.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y.notepad.model.result.Result;
import org.y.notepad.service.UserService;
import org.y.notepad.web.util.WebUtil;

/**
 * 用户控制器, 普通用户注册/编辑基本资料
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 请求Core获取授权码
     *
     * @param userId 用户ID
     */
    @RequestMapping("/login")
    public Result login(int userId) {
        String token = userService.createToken(userId);
        return Result.data(token);
    }
}
