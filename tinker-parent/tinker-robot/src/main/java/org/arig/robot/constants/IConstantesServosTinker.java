package org.arig.robot.constants;

import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.Pin;

public interface IConstantesServosTinker {

    // Constantes d'identification Servo //
    // --------------------------------- //
    Pin FOURCHE = PCA9685Pin.PWM_00;
    Pin BLOCAGE_DROITE = PCA9685Pin.PWM_01;
    Pin BLOCAGE_GAUCHE = PCA9685Pin.PWM_02;
    Pin TRANSLATEUR = PCA9685Pin.PWM_03;

    // Constantes de position //
    // ---------------------- //
    int POS_FOURCHE_OUVERT = 1500;
    int POS_FOURCHE_FERME = 1500;

    int POS_BLOCAGE_DROITE_OUVERT = 1500;
    int POS_BLOCAGE_DROITE_FERME = 1500;

    int POS_BLOCAGE_GAUCHE_OUVERT = 1500;
    int POS_BLOCAGE_GAUCHE_FERME = 1500;

    int POS_TRANSLATEUR_DROITE = 1500;
    int POS_TRANSLATEUR_CENTRE = 1500;
    int POS_TRANSLATEUR_GAUCHE = 1500;

}
