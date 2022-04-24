package org.arig.robot.odin.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.services.OdinIOServiceRobot;
import org.arig.robot.services.OdinServosService;
import org.arig.robot.system.capteurs.CarreFouilleReader;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@ShellCommandGroup("Servos")
@AllArgsConstructor
public class OdinIOCommands {

    private final OdinIOServiceRobot odinIOServiceRobot;
    private final OdinServosService odinServosService;
    private final CarreFouilleReader carreFouilleReader;

    @ShellMethod("Read couleur ventouse")
    public void readCouleurVentouse() {
        odinIOServiceRobot.enableLedCapteurCouleur();
        ThreadUtils.sleep(500);
        CouleurEchantillon echantillonHaut = odinIOServiceRobot.couleurVentouseHaut();
        CouleurEchantillon echantillonBas = odinIOServiceRobot.couleurVentouseBas();
        log.info("Couleur ventouse haut : {}", echantillonHaut);
        log.info("Couleur ventouse bas : {}", echantillonBas);
        odinIOServiceRobot.disableLedCapteurCouleur();
    }

    @SneakyThrows
    @ShellMethod("Read carré de fouille")
    public void readCarreFouille() {
        odinServosService.carreFouilleOhmmetreMesure(true);
        for (int i = 0; i < 10; i++) {
            log.info("Carre de fouille {} / 10 : {}", i, carreFouilleReader.readCarreFouille());
            ThreadUtils.sleep(1000);
        }
        odinServosService.carreFouilleOhmmetreFerme(false);
    }

    @SneakyThrows
    @ShellMethod("State ventouse")
    public void printStateVentouses() {
        for (CouleurEchantillon couleur : CouleurEchantillon.values()) {
            log.info("State ventouse bas : {}", couleur);
            carreFouilleReader.printStateVentouse(couleur, null);
            ThreadUtils.sleep(500);
        }

        for (CouleurEchantillon couleur : CouleurEchantillon.values()) {
            log.info("State ventouse haut : {}", couleur);
            carreFouilleReader.printStateVentouse(null, couleur);
            ThreadUtils.sleep(500);
        }

        carreFouilleReader.printStateVentouse(null, null);
    }

    @SneakyThrows
    @ShellMethod("State stock")
    public void printStateStock() {
        for (int i = 0; i < 6 ; i++) {
            CouleurEchantillon[] couleurs = new CouleurEchantillon[]{null, null, null, null, null, null};
            for (CouleurEchantillon echantillon : CouleurEchantillon.values()) {
                log.info("State stock {} : {}", i+1, echantillon);
                couleurs[i] = echantillon;
                carreFouilleReader.printStateStock(couleurs[0], couleurs[1], couleurs[2], couleurs[3], couleurs[4], couleurs[5]);
                ThreadUtils.sleep(500);
            }
        }

        carreFouilleReader.printStateStock(null, null, null, null, null, null);
    }
}
