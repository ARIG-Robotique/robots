package org.arig.robot.constants;

import org.arig.robot.system.capteurs.TCS34725ColorSensor;

/**
 * @author gdepuille on 21/12/13.
 */
public interface IConstantesI2CSimulator {

    String CODEUR_MOTEUR_DROIT = "Codeur Moteur Droit";
    int CODEUR_DROIT_ADDRESS = 0x30;

    String CODEUR_MOTEUR_GAUCHE = "Codeur Moteur Gauche";
    int CODEUR_GAUCHE_ADDRESS = 0x32;

    String CODEUR_MOTEUR_CAROUSEL = "Codeur Moteur Carousel";
    int CODEUR_CAROUSEL_ADDRESS = 0x34;

    String I2C_ADC_DEVICE_NAME = "I2C ADC";
    int I2C_ADC_ADDRESS = 0x48;

    String SERVO_DEVICE_NAME = "SD21";
    int SD21_ADDRESS = 0x61;

    String TCS34725_DEVICE_NAME = "Front color sensor";
    int TCS34725_ADDRESS = TCS34725ColorSensor.TCS34725_ADDRESS;
}
