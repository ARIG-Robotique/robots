package org.arig.robot.constants;

import org.arig.pi4j.gpio.extension.pcf.PCF8574GpioProvider;

public interface NerellConstantesI2C {

    String CODEUR_MOTEUR_DROIT = "Codeur Moteur Droit";
    int CODEUR_DROIT_ADDRESS = 0x32;

    String CODEUR_MOTEUR_GAUCHE = "Codeur Moteur Gauche";
    int CODEUR_GAUCHE_ADDRESS = 0x30;

    String PCF_ALIM_DEVICE_NAME = "Carte alimentation";
    int PCF_ALIM_ADDRESS = PCF8574GpioProvider.PCF8574A_0x38;

    String PCF1_DEVICE_NAME = "PCF 1";
    int PCF1_ADDRESS = PCF8574GpioProvider.PCF8574_0x25;

    String PCF2_DEVICE_NAME = "PCF 2";
    int PCF2_ADDRESS = PCF8574GpioProvider.PCF8574_0x26;

    String PCA9685_DEVICE_NAME = "PCA9685";
    int PCA9685_ADDRESS = 0x40;

    String SERVO_DEVICE_NAME = "SD21";
    int SD21_ADDRESS = 0x61;

    String ALIM_MESURE_DEVICE_NAME = "Mesure alimentation";
    int ALIM_MESURE_ADDRESS = 0x20;
}
