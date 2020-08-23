package org.y.notepad.web.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.y.notepad.model.entity.User;
import org.y.notepad.model.enu.ErrorCode;
import org.y.notepad.service.UserService;
import org.y.notepad.util.Constants;
import org.y.notepad.web.util.WebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户授权状态拦截
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private final UserService userService;

    public LoginInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // response.setHeader("P3P","CP=\"IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT\"");

        Integer userId = WebUtil.getSession(Constants.KEY_OF_SESSION_USER_ID);
        if (null == userId)
            ErrorCode.NOT_LOGIN.breakOff();

        User user = WebUtil.getUser();
        if (null == user) {
            user = userService.createToken(userId);
            WebUtil.setUser(user);
        }
        return true;
    }
}
