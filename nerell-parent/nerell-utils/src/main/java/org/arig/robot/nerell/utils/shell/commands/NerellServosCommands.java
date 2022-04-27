package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.BrasService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.services.NerellServosService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@ShellComponent
@ShellCommandGroup("Servos")
@AllArgsConstructor
public class NerellServosCommands {

    private final NerellRobotStatus rs;
    private final NerellServosService servosService;
    private final BrasService brasService;
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

    @ShellMethod("Configuration attente moustache gauche")
    public void configWaitMoustacheGauche(int wait) {
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.moustacheGaucheOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.moustacheGaucheFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente moustache droite")
    public void configWaitMoustacheDroite(int wait) {
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.moustacheDroiteOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.moustacheDroiteFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente langue")
    public void configWaitLangue(int wait) {
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.langueOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.langueFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente fourche statuette")
    public void configWaitFourcheStatuette(int wait) {
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.fourcheStatuettePriseDepose(false);
            ThreadUtils.sleep(wait);
            servosService.fourcheStatuetteFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente ohmmetre")
    public void configWaitOhmmetre(int wait) {
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.carreFouilleOhmmetreMesure(false);
            ThreadUtils.sleep(wait);
            servosService.carreFouilleOhmmetreFerme(false);
            ThreadUtils.sleep(wait);
        }
    }

    @ShellMethod("Configuration attente pousse carre fouille")
    public void configWaitPousseCarreFouille(int wait) {
        servosService.carreFouilleOhmmetreOuvert(true);
        for (int i = 0 ; i < nbLoop ; i++) {
            servosService.carreFouillePoussoirPoussette(false);
            ThreadUtils.sleep(wait);
            servosService.carreFouillePoussoirFerme(false);
            ThreadUtils.sleep(wait);
        }
        servosService.carreFouilleOhmmetreFerme(false);
    }

    enum Bras {
        HAUT,
        BAS
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Déplacement de bras")
    public void mouvementBras(Bras bras, int a1, int a2, int a3) {
        switch (bras) {
            case HAUT:
                servosService.brasHaut(a1, a2, a3, 40);
                break;
            case BAS:
                servosService.brasBas(a1, a2, a3, 40);
                break;
        }
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Prise et stockage d'un échantillon au sol")
    public void cyclePriseSol(@ShellOption(defaultValue = "INCONNU") CouleurEchantillon couleur) {
        ioService.couleurVentouseHaut();
        ioService.couleurVentouseBas();

        brasService.prise(BrasService.TypePrise.SOL, couleur);
        brasService.finalizePrise();
        log.info("Stock : {}", Arrays.stream(rs.stock()).map(c -> c == null ? "null" : c.name()).collect(Collectors.joining(",")));
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Dépose d'un échantillon au sol")
    public void cycleDeposeSol() {
        brasService.depose(BrasService.TypeDepose.SOL);
        brasService.finalizeDepose();
        log.info("Stock : {}", Arrays.stream(rs.stock()).map(c -> c == null ? "null" : c.name()).collect(Collectors.joining(",")));
    }
}
