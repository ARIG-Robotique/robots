package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.NerellIOServiceRobot;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@Slf4j
@ShellComponent
@ShellCommandGroup("IO")
@AllArgsConstructor
public class NerellIOCommands {

    private final NerellIOServiceRobot nerellIOServiceRobot;
    private final AbstractEnergyService energyService;

    public Availability alimentationOk() {
        return nerellIOServiceRobot.auOk() && energyService.checkMoteurs()
                ? Availability.available() : Availability.unavailable("Alimentation moteurs KO");
    }

    @ShellMethod("Read all IOs")
    public void readAllIO() {
        log.info("Tirette = {}", nerellIOServiceRobot.tirette());
        log.info("Calage avant gauche = {}", nerellIOServiceRobot.calageAvantGauche());
        log.info("Calage avant droit = {}", nerellIOServiceRobot.calageAvantDroit());
        log.info("Calage arriere gauche = {}", nerellIOServiceRobot.calageArriereGauche());
        log.info("Calage arriere droit = {}", nerellIOServiceRobot.calageArriereDroit());
        log.info("Inductif gauche = {}", nerellIOServiceRobot.inductifGauche());
        log.info("Inductif droit = {}", nerellIOServiceRobot.inductifDroit());
        log.info("Pince avant gauche = {}", nerellIOServiceRobot.pinceAvantGauche());
        log.info("Pince avant centre = {}", nerellIOServiceRobot.pinceAvantCentre());
        log.info("Pince avant droite = {}", nerellIOServiceRobot.pinceAvantDroite());
        log.info("Pince arriere gauche = {}", nerellIOServiceRobot.pinceArriereGauche());
        log.info("Pince arriere centre = {}", nerellIOServiceRobot.pinceArriereCentre());
        log.info("Pince arriere droite = {}", nerellIOServiceRobot.pinceArriereDroite());
        log.info("Présence avant gauche = {}", nerellIOServiceRobot.presenceAvantGauche());
        log.info("Présence avant centre = {}", nerellIOServiceRobot.presenceAvantCentre());
        log.info("Présence avant droite = {}", nerellIOServiceRobot.presenceAvantDroite());
        log.info("Présence arriere gauche = {}", nerellIOServiceRobot.presenceArriereGauche());
        log.info("Présence arriere centre = {}", nerellIOServiceRobot.presenceArriereCentre());
        log.info("Présence arriere droite = {}", nerellIOServiceRobot.presenceArriereDroite());
    }

    @ShellMethod("Enable Electro Aimant")
    @ShellMethodAvailability("alimentationOk")
    public void enableElectroAimant() {
        nerellIOServiceRobot.enableElectroAimant();
    }

    @ShellMethod("Enable Electro Aimant")
    @ShellMethodAvailability("alimentationOk")
    public void disableElectroAimant() {
        nerellIOServiceRobot.disableElectroAimant();
    }

    @ShellMethod("Tourne solar wheel")
    @ShellMethodAvailability("alimentationOk")
    public void tourneSolarWheel(boolean avant) {
        if (avant) {
            nerellIOServiceRobot.tournePanneauAvant();
        } else {
            nerellIOServiceRobot.tournePanneauArriere();
        }
    }

    @ShellMethod("Stop solar wheel")
    @ShellMethodAvailability("alimentationOk")
    public void stopSolarWheel() {
        nerellIOServiceRobot.stopTournePanneau();
    }
}
