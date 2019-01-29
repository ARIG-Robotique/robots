package org.arig.robot.config.spring;

import org.arig.robot.constants.IConstantesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author gdepuille on 20/12/13.
 */
public class RobotNerell extends BootifullApplication {

    public static void main(final String [] args) {
        boot(args);
    }
}
