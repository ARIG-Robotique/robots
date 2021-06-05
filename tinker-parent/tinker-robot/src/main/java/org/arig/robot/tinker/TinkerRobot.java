package org.arig.robot.tinker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@EnableScheduling
@SpringBootApplication
public class TinkerRobot {

    public static void main(final String[] args) throws IOException {
        SpringApplication.run(TinkerRobot.class, args);
    }
}
