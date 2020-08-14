package org.y.notepad.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.y.notepad.model.result.Result;

/**
 * 框架工具
 */
public class XFrameUtil {

    /**
     * 请求Core获取授权
     *
     * @param userId 用户ID
     * @return 获取失败返回null, 否则返回授权码
     */
    public static String authorize(int userId) {

        try {
            RestTemplate rt = new RestTemplate();
            ResponseEntity<Result> ret = rt.postForEntity(Constants.URL.AUTHORIZE + "/" + userId, null, Result.class);
            if (!ret.getStatusCode().is2xxSuccessful())
                return null;

            Result result = ret.getBody();
            if (result.isFlag())
                return String.valueOf(result.getData());

        } catch (RestClientException e) {
            e.printStackTrace();
        }

        return null;
    }

}
