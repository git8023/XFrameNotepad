package org.y.notepad.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y.notepad.model.entity.User;
import org.y.notepad.model.result.Result;
import org.y.notepad.service.UserService;
import org.y.notepad.util.Constants;
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
        WebUtil.setSession(Constants.KEY_OF_SESSION_USER_ID, userId);
        User user = userService.createToken(userId);
        WebUtil.setUser(user);
        return Result.data(user.getToken());
    }
}
