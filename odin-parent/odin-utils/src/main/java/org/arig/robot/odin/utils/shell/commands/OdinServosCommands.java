package org.arig.robot.odin.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.OdinRobotStatus;
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

@Slf4j
@ShellComponent
@ShellCommandGroup("Servos")
@AllArgsConstructor
public class OdinServosCommands {

    private final OdinRobotStatus rs;
    private final OdinServosService servosService;
    private final IOdinIOService ioService;
    private final OdinPincesAvantService pincesAvantService;
    private final OdinPincesArriereService pincesArriereService;

    private final int nbLoop = 3;

    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethod("Récupèration de tension des servos")
    public void getTension() {
        final double tension = servosService.getTension();
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Cycle de préparation des servos")
    public void preparation() {
        servosService.cyclePreparation();
        ThreadUtils.sleep(800);
    }

    private void priseAvant() {
        rs.enablePincesAvant();

        long nbBouees = 0;
        while(nbBouees != 2) {
            nbBouees = Arrays.stream(rs.pincesAvant()).filter(Objects::nonNull).count();
        }

        ThreadUtils.sleep(2000);
    }

    private void priseArriere() {
        rs.enablePincesArriere();

        long nbBouees = 0;
        while(nbBouees != 2) {
            nbBouees = Arrays.stream(rs.pincesArriere()).filter(Objects::nonNull).count();
        }

        ThreadUtils.sleep(2000);
    }

    private void deposeAvant() {
        pincesAvantService.deposeGrandPort();
    }

    private void deposeArriere() {
        pincesArriereService.deposeGrandPort();
    }

    @ShellMethod("Prise avant puis dépose")
    public void cyclePriseAvantPuisDepose() {
        priseAvant();
        deposeAvant();
    }

    @ShellMethod("Prise arriere puis dépose")
    public void cyclePriseArrierePuisDepose() {
        priseArriere();
        deposeArriere();
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
