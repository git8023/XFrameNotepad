package org.y.notepad.event;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.y.notepad.model.result.Result;
import org.y.notepad.util.Constants;
import org.y.notepad.util.StringUtil;

import java.util.List;

@Component
@Slf4j
public class OnStartEvent implements ApplicationRunner {
    @Value("${logging.file.path}")
    private String logFileDir;

    @Override
    public void run(ApplicationArguments args) {
        List<String> mid = args.getOptionValues("mid");
        if (1 == mid.size())
            Constants.MODULE_ID = Integer.parseInt(mid.get(0));

        List<String> token = args.getOptionValues("token");
        if (1 == mid.size())
            Constants.TOKEN = token.get(0);

        log.info("框架 MODULE_ID:" + Constants.MODULE_ID);
        log.info("框架 TOKEN:" + Constants.TOKEN);

        RestTemplate rest = new RestTemplate();
        String url = Constants.URL.MODULE + "/logDir/" + Constants.MODULE_ID
                + "/" + Constants.TOKEN + "/" + StringUtil.base64Enc(logFileDir);

        log.info("修改LogDir: " + url);
        Result result = null;
        try {
            String r = rest.getForObject(url, String.class);
            result = JSON.parseObject(r, Result.class);
        } catch (Exception e) {
            log.warn("请求XFrameCore.module.logDir失败: " + e.getMessage());
        }

        if (null == result)
            throw new RuntimeException("XFrameCore.module.logDir未响应Result数据");

        if (result.isFlag()) {
            log.info("设置日志目录成功");
        } else {
            log.warn("设置日志目录失败: [errorCode: " + result.getErrorCode() + "]" + result.getMessage());
        }

        log.info("启动成功");
    }

}
