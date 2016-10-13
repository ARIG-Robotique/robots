package org.arig.eurobot.constants;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;

/**
 * @author gdepuille on 21/12/13.
 */
public interface IConstantesI2C {

    static String CAPTEUR_RGB = "Capteur RGB";
    static int CAPTEUR_RGB_ADDRESS = 0x29;

    static String CODEUR_MOTEUR_DROIT = "Codeur Moteur Droit";
    static int CODEUR_DROIT_ADDRESS = 0x30;

    static String CODEUR_MOTEUR_GAUCHE = "Codeur Moteur Gauche";
    static int CODEUR_GAUCHE_ADDRESS = 0x32;

    static String PCF_ALIM_DEVICE_NAME = "Carte alimentation";
    static int PCF_ALIM_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3D;

    static String PCF_NUM1_DEVICE_NAME = "Carte numérique 1";
    static int PCF_NUM1_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3E;

    static String PCF_NUM2_DEVICE_NAME = "Carte numérique 2";
    static int PCF_NUM2_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3F;

    static String I2C_ADC_DEVICE_NAME = "I2C ADC";
    static int I2C_ADC_ADDRESS = 0x48;

    static String PROPULSION_DEVICE_NAME = "MD22";
    static int MD22_ADDRESS = 0x58;

    static String SERVO_DEVICE_NAME = "SD21";
    static int SD21_ADDRESS = 0x61;

    static String US_FRONT = "US Front";
    static int US_FRONT_ADDRESS = 0x70;

    static String US_GAUCHE = "US Gauche";
    static int US_GAUCHE_ADDRESS = 0x71;

    static String US_DROIT = "US Droit";
    static int US_DROIT_ADDRESS = 0x72;

    static String US_BACK = "US Back";
    static int US_BACK_ADDRESS = 0x73;
}
