package org.arig.robot.constants;

import org.arig.pi4j.gpio.extension.pcf.PCF8574GpioProvider;

public interface IConstantesI2COdin {

    String CODEUR_MOTEUR_DROIT = "Codeur Moteur Droit";
    int CODEUR_DROIT_ADDRESS = 0x30;

    String CODEUR_MOTEUR_GAUCHE = "Codeur Moteur Gauche";
    int CODEUR_GAUCHE_ADDRESS = 0x32;

    String PCF_ALIM_DEVICE_NAME = "Carte alimentation";
    int PCF_ALIM_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3D;

    String PCF1_DEVICE_NAME = "PCF 1 (Inputs Pololu)";
    int PCF1_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3B;

    String PCF2_DEVICE_NAME = "PCF 2 (Inputs µSwitch)";
    int PCF2_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3F;

    String PCA9685_DEVICE_NAME = "PCA9685";
    int PCA9685_ADDRESS = 0x40;

    String SERVO_DEVICE_NAME = "SD21";
    int SD21_ADDRESS = 0x61;

    String VACUUM_CONTROLLER_DEVICE_NAME = "Vacuum ctrl";
    int VACUUM_CONTROLLER_ADDRESS = 0x26;

    String ALIM_MESURE_DEVICE_NAME = "Mesure alimentation";
    int ALIM_MESURE_ADDRESS = 0x20;

    String MULTIPLEXEUR_I2C_NAME = "Mux TCA";
    int MULTIPLEXEUR_I2C_ADDRESS = 0x70;

    String COULEUR_AVANT_1_NAME = "Couleurs avant 1";
    byte COULEUR_AVANT_1_MUX_CHANNEL = 0;

    String COULEUR_AVANT_2_NAME = "Couleurs avant 2";
    byte COULEUR_AVANT_2_MUX_CHANNEL = 1;

    String COULEUR_ARRIERE_1_NAME = "Couleurs arriere 1";
    byte COULEUR_ARRIERE_1_MUX_CHANNEL = 2;

    String COULEUR_ARRIERE_2_NAME = "Couleurs arriere 2";
    byte COULEUR_ARRIERE_2_MUX_CHANNEL = 3;
}
