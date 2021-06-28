package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.services.INerellPincesArriereService;
import org.arig.robot.services.INerellPincesAvantService;
import org.arig.robot.services.NerellServosService;
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
public class NerellServosCommands {

    private final NerellRobotStatus rs;
    private final NerellServosService servosService;
    private final INerellIOService ioService;
    private final INerellPincesAvantService pincesAvantService;
    private final INerellPincesArriereService pincesArriereService;

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

        StopWatch watch = new StopWatch();
        watch.reset();
        watch.start();

        long nbBouees = 0;
        while(nbBouees != 4) {
            nbBouees = Arrays.stream(rs.pincesAvant()).filter(Objects::nonNull).count();

            if (watch.getTime(TimeUnit.SECONDS) > 5) {
                log.warn("Echappement car trop long");
                break;
            }
        }

        ThreadUtils.sleep(IConstantesNerellConfig.TIME_BEFORE_READ_COLOR * 4);
    }

    private void deposeAvant() {
        pincesAvantService.deposePetitPort();
    }

    @ShellMethod("Cycle poussette (avec ou sans prise avant)")
    public void cyclePoussette(boolean avecPrise) {
        if (avecPrise) {
            priseAvant();
        }

        servosService.ascenseursAvantHaut(true);
        servosService.moustachesOuvert(true);
        ThreadUtils.sleep(5000);

        servosService.moustachesPoussette(true);
        servosService.moustachesOuvert(true);

        if (avecPrise) {
            deposeAvant();
        } else {
            ThreadUtils.sleep(5000);
        }

        servosService.moustachesFerme(false);
    }

    @ShellMethod("Prise ecueil, dépose table")
    public void cyclePriseEcueilDeposeTable() {
        preparation();

        pincesArriereService.preparePriseEcueil();
        ThreadUtils.sleep(5000);
        pincesArriereService.finalisePriseEcueil(ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU);
        ThreadUtils.sleep(2000);
        pincesArriereService.deposePetitPort();
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

    @ShellMethod("Configuration attente moustaches")
    public void configWaitMoustache(int wait) {
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.moustachesOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.moustachesFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration poussette")
    public void configWaitPoussette(int wait) {
        servosService.moustachesOuvert(true);
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.moustachesPoussette(false);
            ThreadUtils.sleep(wait);
            servosService.moustachesOuvert(true);
        }
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

    @ShellMethod("Configuration ascenseurs avant")
    public void configWaitAscenseurAvant(int wait, int nb) {
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.ascenseurAvantBas(nb, false);
            ThreadUtils.sleep(wait);
            servosService.ascenseurAvantHaut(nb, false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration ascenseur arriere")
    public void configWaitAscenseurArriere(int wait, boolean prise, boolean depose) {
        if (prise) {
            servosService.pincesArriereOuvert(false);
            servosService.pivotArriereOuvert(true);
            servosService.ascenseurArriereEcueil(true);
            ThreadUtils.sleep(5000);
            servosService.pincesArriereFerme(true);
            servosService.ascenseurArriereHaut(true);
        }

        servosService.pincesArriereFerme(true);
        servosService.pivotArriereOuvert(true);
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.ascenseurArriereHaut(false);
            ThreadUtils.sleep(wait);
            servosService.ascenseurArriereTable(false);
            ThreadUtils.sleep(wait);
        }

        if (depose) {
            servosService.pincesArriereOuvert(true);
            servosService.ascenseurArriereHaut(true);
            servosService.pincesArriereFerme(false);
            servosService.pivotArriereFerme(false);
        }
    }

    @ShellMethod("Configuration pivot arriere")
    public void configWaitPivotArriere(int wait) {
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.pivotArriereOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.pivotArriereFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration pince arriere")
    public void configWaitPinceArriere(int wait) {
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.pincesArriereOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.pincesArriereFerme(false);
            ThreadUtils.sleep(wait);
        }
    }
}
