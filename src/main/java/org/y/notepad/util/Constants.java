package org.y.notepad.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
public final class Constants {
    public static final String KEY_OF_SESSION_USER = StringUtil.genGUID();
    public static final String KEY_OF_LOGOUT_REASON = StringUtil.genGUID();
    public static final String KEY_OF_SESSION_USER_ID = StringUtil.genGUID();
    // public static final String KEY_OF_NOT_FOUND_URI = "KEY_OF_NOT_FOUND_URI";
    // public static final String KEY_OF_CHECK_CODE = "KEY_OF_CHECK_CODE";
    // public static final String KEY_OF_CHECK_CODE_EXPIRED = "KEY_OF_CHECK_CODE_EXPIRED";

    // public static final String VALUE_OF_INITIAL_PASSWORD = StringUtil.getMD5("123456");
    // public static final int VALUE_OF_CHECK_CODE_EXPIRED_MINUTES = 5;

    public interface URL {

        /**
         * XFrame Core Module request base url
         */
        String MODULE = "http://localhost/module";

        /**
         * 登录授权
         */
        String AUTHORIZE = MODULE + "/auth/" + MODULE_ID + "/" + TOKEN;
    }

    // 内部通信令牌
    public static String TOKEN;
    // 模块在XFrame中的唯一标记
    public static int MODULE_ID;

    /**
     * 校验Token
     *
     * @param mid   模块ID
     * @param token 启动Token
     * @return true-校验成功, false-校验失败
     */
    public static boolean checkToken(@PathVariable int mid, @PathVariable String token) {
        if (Constants.MODULE_ID != mid) {
            log.info("MODULE_ID不匹配, 期望值[" + Constants.MODULE_ID + "]实际值[" + mid + "]");
            return false;
        }
        if (!StringUtils.equals(Constants.TOKEN, token)) {
            log.info("TOKEN不匹配, 期望值[" + Constants.TOKEN + "]实际值[" + token + "]");
            return false;
        }
        return true;
    }
}
