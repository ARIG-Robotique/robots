package org.arig.robot.pami.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.PamiIOServiceRobot;
import org.arig.robot.services.PamiRobotServosService;
import org.arig.robot.system.capteurs.i2c.ARIG2024IoPamiSensors;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@ShellCommandGroup("IO")
@AllArgsConstructor
public class PamiIOCommands {

    private final PamiIOServiceRobot pamiIOServiceRobot;
    private final PamiRobotServosService pamiServosService;
    private final ARIG2024IoPamiSensors arig2024IoPamiSensors;

    @ShellMethod("Identification des servos")
    public void readAllIo() {
        log.info("Lecture des IOs");
        arig2024IoPamiSensors.refreshSensors();
        log.info("IO 1 : {}", arig2024IoPamiSensors.isArriereGauche());
        log.info("IO 2 : {}", arig2024IoPamiSensors.isArriereDroite());
        log.info("GP2D 1 : {}", arig2024IoPamiSensors.getGp2dGauche());
        log.info("GP2D 2 : {}", arig2024IoPamiSensors.getGp2dCentre());
        log.info("GP2D 3 : {}", arig2024IoPamiSensors.getGp2dDroite());
    }
}
