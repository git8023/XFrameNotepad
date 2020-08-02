package org.y.notepad.web.controller;

import lombok.Lombok;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y.notepad.util.Constants;

@Slf4j
@RestController
@RequestMapping({"", "/"})
public class XFrameController {

    @Value("${logging.file.path}")
    private String logFileDir;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 停止当前程序运行
     */
    @RequestMapping("/shutdown/{mid}/{token}")
    public void shutdown(@PathVariable int mid, @PathVariable String token) {
        if (Constants.MODULE_ID != mid) {
            log.info("MODULE_ID不匹配, 期望值[" + Constants.MODULE_ID + "]实际值[" + mid + "]");
            return;
        }
        if (!StringUtils.equals(Constants.TOKEN, token)) {
            log.info("TOKEN不匹配, 期望值[" + Constants.TOKEN + "]实际值[" + token + "]");
            return;
        }

        log.warn("XFrameController.shutdown: 程序退出.");
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw Lombok.sneakyThrow(e);
            }
            SpringApplication.exit(applicationContext);
        }).start();
    }

    /**
     * 获取日志文件目录
     */
    @RequestMapping("/logDir/{mid}/{token}")
    public String logDir(@PathVariable int mid, @PathVariable String token) {
        if (Constants.MODULE_ID != mid) {
            log.info("MODULE_ID不匹配, 期望值[" + Constants.MODULE_ID + "]实际值[" + mid + "]");
            return null;
        }
        if (!StringUtils.equals(Constants.TOKEN, token)) {
            log.info("TOKEN不匹配, 期望值[" + Constants.TOKEN + "]实际值[" + token + "]");
            return null;
        }

        return logFileDir;
    }

}
