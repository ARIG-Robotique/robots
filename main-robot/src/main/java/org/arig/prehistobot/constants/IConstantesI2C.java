package org.arig.prehistobot.constants;

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
}
