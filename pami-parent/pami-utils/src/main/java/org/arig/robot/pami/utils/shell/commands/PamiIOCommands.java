package org.arig.robot.pami.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.model.RobotName;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.PamiIOServiceRobot;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.system.capteurs.i2c.ARIG2025IoPamiSensors;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.leds.ARIG2025IoPamiLeds;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ShellComponent
@ShellCommandGroup("IO")
@AllArgsConstructor
public class PamiIOCommands {

    private final ARIG2025IoPamiSensors arig2025IoPamiSensors;
    private final ARIG2025IoPamiLeds arig2025IoPamiLeds;

    private final PamiIOServiceRobot pamiIOServiceRobot;
    private final RobotName robotName;

    @ShellMethod("Identification des IOs")
    public void readAllIo() {
        log.info("Lecture des IOs");
        log.info("Input 1 : {}", arig2025IoPamiSensors.isInput1());
        log.info("Input 2 : {}", arig2025IoPamiSensors.isInput2());
        log.info("Input 3 : {}", arig2025IoPamiSensors.isInput3());
        log.info("Input 4 : {}", arig2025IoPamiSensors.isInput4());
        log.info("Input 5 : {}", arig2025IoPamiSensors.isInput5());
        log.info("Input 6 : {}", arig2025IoPamiSensors.isInput6());
        log.info("Input 7 : {}", arig2025IoPamiSensors.isInput7());

        log.info("\n===================================================\n");
        log.info("{} real IOs name", robotName.name());
        log.info("AU : {}", pamiIOServiceRobot.auOk());
        log.info("Calage arriere gauche : {}", pamiIOServiceRobot.calageArriereGauche());
        log.info("Calage arriere droit : {}", pamiIOServiceRobot.calageArriereDroit());
        log.info("Sol gauche : {}", pamiIOServiceRobot.presenceSolGauche(true));
        log.info("Sol droit : {}", pamiIOServiceRobot.presenceSolDroit(true));
    }

    @ShellMethod
    public void testLeds() {
        arig2025IoPamiLeds.setAllLeds(ARIG2025IoPamiLeds.LedColor.Black);
        for (ARIG2025IoPamiLeds.LedColor c : ARIG2025IoPamiLeds.LedColor.values()) {
            arig2025IoPamiLeds.setLedAU(c);
            ThreadUtils.sleep(500);
        }
        for (ARIG2025IoPamiLeds.LedColor c : ARIG2025IoPamiLeds.LedColor.values()) {
            arig2025IoPamiLeds.setLedTeam(c);
            ThreadUtils.sleep(500);
        }
        for (ARIG2025IoPamiLeds.LedColor c : ARIG2025IoPamiLeds.LedColor.values()) {
            arig2025IoPamiLeds.setLedCalage(c);
            ThreadUtils.sleep(500);
        }
        for (ARIG2025IoPamiLeds.LedColor c : ARIG2025IoPamiLeds.LedColor.values()) {
            arig2025IoPamiLeds.setLedCentrale(c);
            ThreadUtils.sleep(500);
        }
        for (ARIG2025IoPamiLeds.LedColor c : ARIG2025IoPamiLeds.LedColor.values()) {
            arig2025IoPamiLeds.setAllLeds(c);
            ThreadUtils.sleep(500);
        }
        arig2025IoPamiLeds.setAllLeds(ARIG2025IoPamiLeds.LedColor.Black);
    }
}
