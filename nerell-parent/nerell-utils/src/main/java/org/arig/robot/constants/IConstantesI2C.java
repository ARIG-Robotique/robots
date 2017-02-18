package org.arig.robot.constants;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;

/**
 * @author gdepuille on 21/12/13.
 */
public interface IConstantesI2C {

    String CODEUR_MOTEUR_DROIT = "Codeur Moteur Droit";
    int CODEUR_DROIT_ADDRESS = 0x30;

    String CODEUR_MOTEUR_GAUCHE = "Codeur Moteur Gauche";
    int CODEUR_GAUCHE_ADDRESS = 0x32;

    String SERVO_DEVICE_NAME = "SD21";
    int SD21_ADDRESS = 0x61;

    String PCF_ALIM_DEVICE_NAME = "Carte alimentation";
    int PCF_ALIM_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3D;

    String TCS34725_DEVICE_NAME = "Front color sensor";
    int TCS34725_ADDRESS = TCS34725ColorSensor.TCS34725_ADDRESS;
}
