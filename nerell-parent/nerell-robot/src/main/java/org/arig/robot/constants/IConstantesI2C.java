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

    String PCF_ALIM_DEVICE_NAME = "Carte alimentation";
    int PCF_ALIM_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3D;

    String PCF_NUM1_DEVICE_NAME = "Carte numérique 1";
    int PCF_NUM1_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3E;

    String PCF_NUM2_DEVICE_NAME = "Carte numérique 2";
    int PCF_NUM2_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3F;

    String I2C_ADC_DEVICE_NAME = "I2C ADC";
    int I2C_ADC_ADDRESS = 0x48;

    String PROPULSION_DEVICE_NAME = "MD22";
    int MD22_ADDRESS = 0x58;

    String SERVO_DEVICE_NAME = "SD21";
    int SD21_ADDRESS = 0x61;

    String TCS34725_DEVICE_NAME = "Front color sensor";
    int TCS34725_ADDRESS = TCS34725ColorSensor.TCS34725_ADDRESS;

    String US_FRONT = "US Front";
    int US_FRONT_ADDRESS = 0x70;

    String US_GAUCHE = "US Gauche";
    int US_GAUCHE_ADDRESS = 0x71;

    String US_DROIT = "US Droit";
    int US_DROIT_ADDRESS = 0x72;

    String US_BACK = "US Back";
    int US_BACK_ADDRESS = 0x73;
}
