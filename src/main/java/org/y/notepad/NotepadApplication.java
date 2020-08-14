package org.y.notepad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "org.y.notepad.event",
        "org.y.notepad.web.controller",
        "org.y.notepad.web.converter",
        "org.y.notepad.web.interceptor",
        "org.y.notepad.config",
        "org.y.notepad.service.impl",
        "org.y.notepad.repository",
})
public class NotepadApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotepadApplication.class, args);
    }

}
