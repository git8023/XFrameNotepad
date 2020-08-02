package org.y.notepad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "org.y.notepad.event",
        "org.y.notepad.web.controller"
})
public class NotepadApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotepadApplication.class, args);
    }

}
