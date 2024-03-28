package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.services.NerellIOServiceRobot;
import org.arig.robot.services.NerellServosService;
import org.arig.robot.system.capteurs.CarreFouilleReader;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@Slf4j
@ShellComponent
@ShellCommandGroup("IO")
@AllArgsConstructor
public class NerellIOCommands {

    private final NerellIOServiceRobot nerellIOServiceRobot;
    private final NerellServosService nerellServosService;

    @ShellMethod("Read all IOs")
    public void readAllIO() {
        log.info("In 1 : 1 = {}", nerellIOServiceRobot.in1_1());
        log.info("In 1 : 2 = {}", nerellIOServiceRobot.in1_2());
        log.info("In 1 : 3 = {}", nerellIOServiceRobot.in1_3());
        log.info("In 1 : 4 = {}", nerellIOServiceRobot.in1_4());
        log.info("In 1 : 5 = {}", nerellIOServiceRobot.in1_5());
        log.info("In 1 : 6 = {}", nerellIOServiceRobot.in1_6());
        log.info("In 1 : 7 = {}", nerellIOServiceRobot.in1_7());
        log.info("In 1 : 8 = {}", nerellIOServiceRobot.in1_8());
        log.info("In 2 : 1 = {}", nerellIOServiceRobot.in2_1());
        log.info("In 2 : 2 = {}", nerellIOServiceRobot.in2_2());
        log.info("In 2 : 3 = {}", nerellIOServiceRobot.in2_3());
        log.info("In 2 : 4 = {}", nerellIOServiceRobot.in2_4());
        log.info("In 2 : 5 = {}", nerellIOServiceRobot.in2_5());
        log.info("In 2 : 6 = {}", nerellIOServiceRobot.in2_6());
        log.info("In 2 : 7 = {}", nerellIOServiceRobot.in2_7());
        log.info("In 2 : 8 = {}", nerellIOServiceRobot.in2_8());
        log.info("In 3 : 1 = {}", nerellIOServiceRobot.in3_1());
        log.info("In 3 : 2 = {}", nerellIOServiceRobot.in3_2());
        log.info("In 3 : 3 = {}", nerellIOServiceRobot.in3_3());
        log.info("In 3 : 4 = {}", nerellIOServiceRobot.in3_4());
        log.info("In 3 : 5 = {}", nerellIOServiceRobot.in3_5());
        log.info("In 3 : 6 = {}", nerellIOServiceRobot.in3_6());
        log.info("In 3 : 7 = {}", nerellIOServiceRobot.in3_7());
        log.info("In 3 : 8 = {}", nerellIOServiceRobot.in3_8());
    }
}
