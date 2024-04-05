package org.arig.robot.constants;

import org.arig.pi4j.gpio.extension.pcf.PCF8574GpioProvider;

public interface PamiConstantesI2C {

    String CODEUR_MOTEUR_DROIT = "Codeur Moteur Droit";
    int CODEUR_DROIT_ADDRESS = 0x30;

    String CODEUR_MOTEUR_GAUCHE = "Codeur Moteur Gauche";
    int CODEUR_GAUCHE_ADDRESS = 0x32;

    String PCF_ALIM_DEVICE_NAME = "Carte alimentation";
    int PCF_ALIM_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3D;

    String PCF1_DEVICE_NAME = "PCF 1 (Inputs µSwitch)";
    int PCF1_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3F;

    String PCF2_DEVICE_NAME = "PCF 2 (Inputs Pololu)";
    int PCF2_ADDRESS = PCF8574GpioProvider.PCF8574A_0x3B;

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

    String COULEUR_VENTOUSE_BAS_NAME = "Couleur ventouse bas";
    byte COULEUR_VENTOUSE_BAS_MUX_CHANNEL = 4;

    String COULEUR_VENTOUSE_HAUT_NAME = "Couleur ventouse haut";
    byte COULEUR_VENTOUSE_HAUT_MUX_CHANNEL = 5;
}
