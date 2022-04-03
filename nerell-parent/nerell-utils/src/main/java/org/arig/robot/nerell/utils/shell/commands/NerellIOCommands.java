package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.services.NerellIOServiceRobot;
import org.arig.robot.system.capteurs.CarreFouilleReader;
import org.arig.robot.system.capteurs.TCA9548MultiplexerI2C;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.ColorData;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@ShellCommandGroup("Servos")
@AllArgsConstructor
public class NerellIOCommands {

    private final NerellIOServiceRobot nerellIOServiceRobot;
    private final TCA9548MultiplexerI2C mux;
    private final TCS34725ColorSensor couleurVentouseBas;
    private final CarreFouilleReader carreFouilleReader;

    @ShellMethod("Identification capteur couleur")
    public void identificationCouleur(byte id) {
        mux.selectChannel(id);
        ColorData cd = couleurVentouseBas.getColorData();
        log.info("Couleur capteur {} : R {} - G {} - B {} - #{}", id, cd.r(), cd.g(), cd.b(), cd.hexColor());
    }

    @ShellMethod("Read couleur ventouse")
    public void readCouleurVentouse() {
        nerellIOServiceRobot.enableLedCapteurCouleur();
        ThreadUtils.sleep(500);
        CouleurEchantillon echantillonHaut = nerellIOServiceRobot.couleurVentouseHaut();
        CouleurEchantillon echantillonBas = nerellIOServiceRobot.couleurVentouseBas();
        log.info("Couleur ventouse haut : {}", echantillonHaut);
        log.info("Couleur ventouse bas : {}", echantillonBas);
        nerellIOServiceRobot.disableLedCapteurCouleur();
    }

    @SneakyThrows
    @ShellMethod("Read carré de fouille")
    public void readCarreFouille() {
        log.info("Carre de fouille : {}", carreFouilleReader.readCarreFouille());
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
