package org.y.notepad.service;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 请求Core获取授权码
     * @param userId 用户ID
     * @return 授权码
     */
    String createToken(int userId);

}
