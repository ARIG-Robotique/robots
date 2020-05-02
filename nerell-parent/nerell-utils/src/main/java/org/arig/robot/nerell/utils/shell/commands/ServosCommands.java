package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.services.IPincesAvantService;
import org.arig.robot.services.ServosService;
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
public class ServosCommands {

    private final RobotStatus rs;
    private final ServosService servosService;
    private final IIOService ioService;
    private final IPincesAvantService pincesAvantService;
    private final IPincesArriereService pincesArriereService;

    private final int nbLoop = 3;

    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Cycle de préparation des servos")
    public void preparation() {
        servosService.cyclePreparation();
    }

    @ShellMethod("Récupèration de tension des servos")
    public void getTension() {
        final double tension = servosService.getTension();
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
    public void cyclePriseAvantPuisDepose() {
        rs.enablePincesAvant();

        long nbBouees = 0;
        while(nbBouees != 4) {
            nbBouees = Arrays.stream(rs.pincesAvant()).filter(Objects::nonNull).count();
        }

        rs.disablePincesAvant();
        ThreadUtils.sleep(2000);

        pincesAvantService.deposePetitPort();
        ThreadUtils.sleep(5000);
        pincesAvantService.finaliseDepose();

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

    @ShellMethod("Configuration ascenseur avant")
    public void configWaitAscenseurAvant(int wait, boolean prise, boolean depose) {
        if (prise) {
            servosService.ascenseurAvantBas(true);
            servosService.pincesAvantOuvert(true);
            ThreadUtils.sleep(5000);
            servosService.pincesAvantPrise(true);
        }

        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.ascenseurAvantOuvertureMoustache(false);
            ThreadUtils.sleep(wait);
            servosService.ascenseurAvantBas(false);
            ThreadUtils.sleep(wait);
        }

        if (depose) {
            servosService.pincesAvantOuvert(false);
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

    @ShellMethod("Configuration pince avant")
    public void configWaitPinceAvant(int wait) {
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.pincesAvantOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.pincesAvantFerme(false);
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
