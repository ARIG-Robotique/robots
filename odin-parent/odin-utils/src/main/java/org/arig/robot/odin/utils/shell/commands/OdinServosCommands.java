package org.arig.robot.odin.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IConstantesOdinConfig;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.IOdinIOService;
import org.arig.robot.services.OdinPincesArriereService;
import org.arig.robot.services.OdinPincesAvantService;
import org.arig.robot.services.OdinServosService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@ShellComponent
@ShellCommandGroup("Servos")
@AllArgsConstructor
public class OdinServosCommands {

    private final OdinRobotStatus rs;
    private final OdinServosService servosService;
    private final IOdinIOService ioService;
    private final AbstractEnergyService energyService;
    private final OdinPincesAvantService pincesAvantService;
    private final OdinPincesArriereService pincesArriereService;

    private final int nbLoop = 3;

    public Availability alimentationOk() {
        return ioService.auOk() && energyService.checkServos() && energyService.checkMoteurs()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Cycle de préparation des servos")
    public void preparation() {
        servosService.cyclePreparation();
        ThreadUtils.sleep(800);
    }

    private void priseAvant() {
        rs.enablePincesAvant();

        StopWatch watch = new StopWatch();
        watch.reset();
        watch.start();

        long nbBouees = 0;
        while(nbBouees != 2) {
            nbBouees = Arrays.stream(rs.pincesAvant()).filter(Objects::nonNull).count();

            if (watch.getTime(TimeUnit.SECONDS) > 5) {
                log.warn("Echappement car trop long");
                break;
            }
        }

        ThreadUtils.sleep(IConstantesOdinConfig.TIME_BEFORE_READ_COLOR * 4);
    }

    private void priseArriere() {
        rs.enablePincesArriere();

        StopWatch watch = new StopWatch();
        watch.reset();
        watch.start();
        long nbBouees = 0;
        while(nbBouees != 2) {
            nbBouees = Arrays.stream(rs.pincesArriere()).filter(Objects::nonNull).count();

            if (watch.getTime(TimeUnit.SECONDS) > 5) {
                log.warn("Echappement car trop long");
                break;
            }
        }

        ThreadUtils.sleep(IConstantesOdinConfig.TIME_BEFORE_READ_COLOR * 4);
    }

    private void deposeAvant() {
        pincesAvantService.deposeGrandPort();
    }

    private void deposeArriere() {
        pincesArriereService.deposeGrandPort();
    }

    @ShellMethod("Prise avant puis dépose")
    public void cyclePriseAvantPuisDepose(int nb) {
        int cpt = 0;
        do {
            priseAvant();
            deposeAvant();
            cpt++;
        } while(cpt < nb);
    }

    @ShellMethod("Prise arriere puis dépose")
    public void cyclePriseArrierePuisDepose(int nb) {
        int cpt = 0;
        do {
            priseArriere();
            deposeArriere();
            cpt++;
        } while(cpt < nb);
    }

    @ShellMethod("Configuration attente bras gauche")
    public void configWaitBrasGauche(int wait) {
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.brasGauchePhare(false);
            ThreadUtils.sleep(wait);
            servosService.brasGaucheFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente bras droit")
    public void configWaitBrasDroit(int wait) {
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.brasDroitPhare(false);
            ThreadUtils.sleep(wait);
            servosService.brasDroitFerme(false);
            ThreadUtils.sleep(wait);
        }
    }
}
