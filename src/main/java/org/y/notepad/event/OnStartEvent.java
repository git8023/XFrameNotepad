package org.y.notepad.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.y.notepad.util.Constants;

import java.util.List;

@Component
@Slf4j
public class OnStartEvent implements ApplicationRunner {
    @Value("${logging.file.path}")
    private String logFileDir;

    @Autowired
    private ApplicationArguments arguments;

    @Override
    public void run(ApplicationArguments args) {
        List<String> mid = arguments.getOptionValues("mid");
        if (1 == mid.size())
            Constants.MODULE_ID = Integer.parseInt(mid.get(0));

        List<String> token = arguments.getOptionValues("token");
        if (1 == mid.size())
            Constants.TOKEN = token.get(0);

        log.info("框架 MODULE_ID:" + Constants.MODULE_ID + "/" + Constants.TOKEN);
        log.info("本次 TOKEN:" + Constants.TOKEN);

        RestTemplate rest = new RestTemplate();
        String url = "http://localhost/module/logDir/" + Constants.MODULE_ID
                + "/" + Constants.TOKEN + "/" + logFileDir;
        rest.getForObject(url, Void.class);

        log.info("启动成功");
    }

}
