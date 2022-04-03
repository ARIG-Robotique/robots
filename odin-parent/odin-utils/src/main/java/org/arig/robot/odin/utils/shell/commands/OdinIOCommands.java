package org.arig.robot.odin.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CouleurEchantillon;
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
public class OdinIOCommands {

    private final TCA9548MultiplexerI2C mux;
    private final TCS34725ColorSensor couleurVentouseBas;
    private final CarreFouilleReader carreFouilleReader;

    @ShellMethod("Identification capteur couleur")
    public void identificationCouleur(byte id) {
        mux.selectChannel(id);
        ColorData cd = couleurVentouseBas.getColorData();
        log.info("Couleur capteur {} : R {} - G {} - B {} - #{}", id, cd.r(), cd.g(), cd.b(), cd.hexColor());
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
            carreFouilleReader.printStateVentouse(couleur, null);
            ThreadUtils.sleep(500);
        }
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
    }
}
