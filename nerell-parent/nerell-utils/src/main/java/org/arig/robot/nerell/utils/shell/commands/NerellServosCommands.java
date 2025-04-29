package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.services.NerellRobotServosService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@ShellCommandGroup("Servos")
@AllArgsConstructor
public class NerellServosCommands {

    private final NerellRobotServosService servosService;
    private final NerellIOService ioService;
    private final AbstractEnergyService energyService;

    private final int nbLoop = 5;

    public Availability alimentationOk() {
        return ioService.auOk() && energyService.checkServos()
                ? Availability.available() : Availability.unavailable("Alimentation servos KO");
    }

    @ShellMethod("Cycle de préparation des servos")
    public void preparation() {
        servosService.cyclePreparation();
        ThreadUtils.sleep(800);
    }

    @ShellMethod("Identification des servos")
    public void identificationServos(byte id, int delta, byte speed, int nbCycle) {
        for (int i = 0; i < nbCycle; i++) {
            servosService.setPositionById(id, 1500 + delta, speed);
            ThreadUtils.sleep(1000);
            servosService.setPositionById(id, 1500 - delta, speed);
            ThreadUtils.sleep(1000);
        }
        servosService.setPositionById(id, 1500, speed);
    }

    @ShellMethod("Configuration attente pince avant")
    public void configWaitPinceAvant(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.groupePincesAvantOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.groupePincesAvantFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente pince arriere")
    public void configWaitPinceArriere(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.groupePincesArriereOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.groupePincesArriereFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente doigts avant")
    public void configWaitDoigtsAvant(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.groupeDoigtsAvantOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.groupeDoigtsAvantFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente doigts arriere")
    public void configWaitDoigtsArriere(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.groupeDoigtsArriereOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.groupeDoigtsArriereFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente block colonne avant")
    public void configWaitBlockColonneAvant(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.groupeBlockColonneAvantOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.groupeBlockColonneAvantFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente block colonne arriere")
    public void configWaitBlockColonneArriere(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.groupeBlockColonneArriereOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.groupeBlockColonneArriereFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente ascenseur avant")
    public void configWaitAscenseurAvant(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.ascenseurAvantHaut(false);
            ThreadUtils.sleep(wait);
            servosService.ascenseurAvantBas(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente ascenseur arriere")
    public void configWaitAscenseurArriere(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.ascenseurArriereHaut(false);
            ThreadUtils.sleep(wait);
            servosService.ascenseurArriereBas(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente tiroir avant")
    public void configWaitTiroirAvant(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.tiroirAvantOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.tiroirAvantStock(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente tiroir arriere")
    public void configWaitTiroirArriere(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.tiroirArriereOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.tiroirArriereStock(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente bec avant")
    public void configWaitBecAvant(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.becAvantOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.becAvantFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente bec arriere")
    public void configWaitBecArriere(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.becArriereOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.becArriereFerme(false);
            ThreadUtils.sleep(wait);
        }
    }
}
