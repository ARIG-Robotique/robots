package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Bras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.BrasService;
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
    private final BrasService bras;

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

    @ShellMethod("Configuration attente pince")
    public void configWaitPince(int wait) {
        bras.setBrasAvant(PositionBras.CALLAGE_PANNEAUX);

        for (int i = 0; i < nbLoop; i++) {
            servosService.groupePinceAvantOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.groupePinceAvantFerme(false);
            ThreadUtils.sleep(wait);
        }

        bras.setBrasAvant(PositionBras.INIT);
    }

    @ShellMethod("Configuration attente moustache")
    public void configWaitBloquePlante(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.groupeBloquePlanteOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.groupeBloquePlanteFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente ski")
    public void configWaitSki(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.setPanneauSolaireSkiOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.setPanneauSolaireSkiFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente roue")
    public void configWaitRoue(int wait) {
        for (int i = 0; i < nbLoop; i++) {
            servosService.setPanneauSolaireRoueOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.setPanneauSolaireRoueFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente glissiere")
    public void configWaitGlissiere(int wait) {
        bras.setBrasArriere(PositionBras.CALLAGE_PANNEAUX);

        for (int i = 0; i < nbLoop; i++) {
            servosService.setPortePotGlissiereSorti(false);
            ThreadUtils.sleep(wait);
            servosService.setPortePotGlissiereRentre(false);
            ThreadUtils.sleep(wait);
        }

        bras.setBrasArriere(PositionBras.INIT);
    }

    @ShellMethod("Configuration attente porte pot")
    public void configWaitPortePot(int wait) {
        bras.setBrasArriere(PositionBras.CALLAGE_PANNEAUX);
        servosService.setPortePotGlissiereSorti(true);

        for (int i = 0; i < nbLoop; i++) {
            servosService.setPortePotHaut(false);
            ThreadUtils.sleep(wait);
            servosService.setPortePotBas(false);
            ThreadUtils.sleep(wait);
        }

        servosService.setPortePotGlissiereRentre(true);
        bras.setBrasArriere(PositionBras.INIT);
    }
}
