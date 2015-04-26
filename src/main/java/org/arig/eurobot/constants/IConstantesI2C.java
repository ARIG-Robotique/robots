package org.arig.eurobot.constants;

import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;

/**
 * Created by mythril on 21/12/13.
 */
public interface IConstantesI2C {

    static String SERVO_DEVICE_NAME = "SD21";
    static int SD21_ADDRESS = 0x61;

    static String PROPULSION_DEVICE_NAME = "MD22";
    static int MD22_ADDRESS = 0x58;

    static String CODEUR_MOTEUR_GAUCHE = "Codeur Moteur Gauche";
    static int CODEUR_GAUCHE_ADDRESS = 0x32;

    static String CODEUR_MOTEUR_DROIT = "Codeur Moteur Droit";
    static int CODEUR_DROIT_ADDRESS = 0x30;

    static String PCF_ALIM_DEVICE_NAME = "Carte alimentation";
    static int PCF_ALIM_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3D;

    static String PCF_NUM1_DEVICE_NAME = "Carte numérique 1";
    static int PCF_NUM1_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3E;

    static String PCF_NUM2_DEVICE_NAME = "Carte numérique 2";
    static int PCF_NUM2_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3F;
}
