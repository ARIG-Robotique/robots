package org.arig.robot.constants;

import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.Pin;

public interface IConstantesServosTinker {

    // Constantes d'identification Servo //
    // --------------------------------- //
    Pin FOURCHE = PCA9685Pin.PWM_03;
    Pin BLOCAGE_DROITE = PCA9685Pin.PWM_00;
    Pin BLOCAGE_GAUCHE = PCA9685Pin.PWM_02;
    Pin TRANSLATEUR = PCA9685Pin.PWM_01;

    Pin MOTEUR_GAUCHE = PCA9685Pin.PWM_14;
    Pin MOTEUR_DROIT = PCA9685Pin.PWM_15;

    // Constantes de position //
    // ---------------------- //
    int POS_FOURCHE_HAUT = -25;
    int POS_FOURCHE_BAS = 25;

    int POS_BLOCAGE_DROITE_OUVERT = -25;
    int POS_BLOCAGE_DROITE_FERME = 25;

    int POS_BLOCAGE_GAUCHE_OUVERT = -25;
    int POS_BLOCAGE_GAUCHE_FERME = 25;

    int POS_TRANSLATEUR_DROITE = -25;
    int POS_TRANSLATEUR_CENTRE = 0;
    int POS_TRANSLATEUR_GAUCHE = 25;

}
