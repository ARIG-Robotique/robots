package org.arig.eurobot.constants;

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

    static String PCF_ALIM = "Carte alimentation";
    static int PCF_ALIM_ADDRESS = 0x3D;

    static String PCF_NUM1 = "Carte numérique 1";
    static int PCF_NUM1_ADDRESS = 0x3E;

    static String PCF_NUM2 = "Carte numérique 2";
    static int PCF_NUM2_ADDRESS = 0x3F;
}
