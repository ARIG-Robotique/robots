package org.arig.prehistobot.web;

import lombok.Setter;
import org.arig.prehistobot.MainRobot;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mythril on 21/12/13.
 */
@RestController
public class ShutdownController implements ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    @RequestMapping("/shutdown")
    public String shutdown() {
        try {
            return "Arret en cours ...";
        } finally {
            MainRobot.shutdown();
        }
    }
}
