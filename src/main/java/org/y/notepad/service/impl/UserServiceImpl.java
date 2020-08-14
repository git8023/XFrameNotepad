package org.y.notepad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.y.notepad.model.entity.User;
import org.y.notepad.model.enu.ErrorCode;
import org.y.notepad.repository.UserRepository;
import org.y.notepad.service.UserService;
import org.y.notepad.util.StringUtil;
import org.y.notepad.util.XFrameUtil;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String createToken(int userId) {
        String token = XFrameUtil.authorize(userId);
        if (StringUtil.isBlank(token))
            ErrorCode.DENIED_OPERATION.breakOff();

        User user = new User();
        user.setUserId(userId);
        user.setToken(token);
        user.setCreateDate(new Date());
        userRepository.JPA.save(user);

        return token;
    }

}
