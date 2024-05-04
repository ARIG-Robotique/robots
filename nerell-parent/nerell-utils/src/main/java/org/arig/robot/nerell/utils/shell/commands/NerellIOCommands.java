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
        log.info("Stock gauche = {}", nerellIOServiceRobot.presenceStockGauche());
        log.info("Stock centre = {}", nerellIOServiceRobot.presenceStockCentre());
        log.info("Stock droite = {}", nerellIOServiceRobot.presenceStockDroite());
        log.info("Pince avant gauche = {}", nerellIOServiceRobot.pinceAvantGauche(false));
        log.info("Pince avant centre = {}", nerellIOServiceRobot.pinceAvantCentre(false));
        log.info("Pince avant droite = {}", nerellIOServiceRobot.pinceAvantDroite(false));
        log.info("Pince arriere gauche = {}", nerellIOServiceRobot.pinceArriereGauche(false));
        log.info("Pince arriere centre = {}", nerellIOServiceRobot.pinceArriereCentre(false));
        log.info("Pince arriere droite = {}", nerellIOServiceRobot.pinceArriereDroite(false));
        log.info("Présence avant gauche = {}", nerellIOServiceRobot.presenceAvantGauche(false));
        log.info("Présence avant centre = {}", nerellIOServiceRobot.presenceAvantCentre(false));
        log.info("Présence avant droite = {}", nerellIOServiceRobot.presenceAvantDroite(false));
        log.info("Présence arriere gauche = {}", nerellIOServiceRobot.presenceArriereGauche(false));
        log.info("Présence arriere centre = {}", nerellIOServiceRobot.presenceArriereCentre(false));
        log.info("Présence arriere droite = {}", nerellIOServiceRobot.presenceArriereDroite(false));
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
            nerellIOServiceRobot.tournePanneauBleu();
        } else {
            nerellIOServiceRobot.tournePanneauJaune();
        }
    }

    @ShellMethod("Stop solar wheel")
    @ShellMethodAvailability("alimentationOk")
    public void stopSolarWheel() {
        nerellIOServiceRobot.stopTournePanneau();
    }
}
