package org.arig.robot.pami.utils.shell.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.PamiIOService;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@Slf4j
@ShellComponent
@ShellCommandGroup("Moteurs")
@RequiredArgsConstructor
public class PamiMoteursCommands {

    private final AbstractRobotStatus rs;
    private final PamiIOService ioService;
    private final AbstractEnergyService energyService;
    private final AbstractPropulsionsMotors propulsionsMotors;
    private final Abstract2WheelsEncoders wheelsEncoders;

    public Availability alimentationOk() {
        return ioService.auOk() && energyService.checkMoteurs()
                ? Availability.available() : Availability.unavailable("Alimentation moteurs KO");
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Rotation des moteurs de propulsions")
    public void moteursPropulsions(final int droite, final int gauche) {
        rs.enableCapture();
        propulsionsMotors.generateMouvement(gauche, droite);
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Arret des moteurs de propulsions")
    public void stopMoteursPropulsions() {
        propulsionsMotors.stopAll();
        rs.disableCapture();
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Test déplacement a balles")
    public void moteursABalle(int wait) {
        rs.enableCapture();
        propulsionsMotors.generateMouvement(propulsionsMotors.getMaxSpeed(), propulsionsMotors.getMaxSpeed());
        ThreadUtils.sleep(wait);

        propulsionsMotors.generateMouvement(propulsionsMotors.getStopSpeed(), propulsionsMotors.getStopSpeed());
        ThreadUtils.sleep(1000);

        propulsionsMotors.generateMouvement(-propulsionsMotors.getMaxSpeed(), propulsionsMotors.getMaxSpeed());
        ThreadUtils.sleep(wait);

        propulsionsMotors.generateMouvement(propulsionsMotors.getStopSpeed(), propulsionsMotors.getStopSpeed());
        rs.disableCapture();
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Reglages des limites de moteurs")
    public void limitMotors() {
        int limitRightUp = -1;
        int limitRightDown = -1;
        int limitLeftUp = -1;
        int limitLeftDown = -1;

        int cmdRight = 0;
        int cmdLeft = 0;

        int cycle = 0;

        rs.enableCapture();

        wheelsEncoders.reset();
        do {
            propulsionsMotors.generateMouvement(cmdLeft, cmdRight);
            log.info("Cycle UP {} : L {} / R {}", cycle, cmdLeft, cmdRight);
            ThreadUtils.sleep(100);
            wheelsEncoders.lectureValeurs();
            if (wheelsEncoders.getGauche() > 1) {
                limitLeftUp = cmdLeft;
            } else {
                cmdLeft += 10;
            }
            if (wheelsEncoders.getDroit() > 1) {
                limitRightUp = cmdRight;
            } else {
                cmdRight += 10;
            }
        } while (limitLeftUp == -1 || limitRightUp == -1);

        wheelsEncoders.reset();
        do {
            propulsionsMotors.generateMouvement(cmdLeft, cmdRight);
            log.info("Cycle DOWN {} : L {} / R {}", cycle, cmdLeft, cmdRight);
            ThreadUtils.sleep(100);
            wheelsEncoders.lectureValeurs();
            if (wheelsEncoders.getGauche() < 1) {
                limitLeftDown = cmdLeft;
            } else {
                cmdLeft -= 10;
            }
            if (wheelsEncoders.getDroit() < 1) {
                limitRightDown = cmdRight;
            } else {
                cmdRight -= 10;
            }
        } while (limitLeftDown == -1 || limitRightDown == -1);

        log.info("Limit Right UP : {} / Limit Right Down : {}", limitRightUp, limitRightDown);
        log.info("Limit Left UP  : {} / Limit Left Down  : {}", limitLeftUp, limitLeftDown);

        propulsionsMotors.stopAll();
        ThreadUtils.sleep(500);
        wheelsEncoders.reset();

        rs.disableCapture();
    }
}
