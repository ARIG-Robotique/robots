package org.arig.robot.constants;

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
}
