package org.arig.robot.tinker.constants;

import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.Pin;

public interface TinkerConstantesServos {

    // Constantes d'identification Servo //
    // --------------------------------- //
    Pin FOURCHE = PCA9685Pin.PWM_03;
    Pin BLOCAGE_DROITE = PCA9685Pin.PWM_00;
    Pin BLOCAGE_GAUCHE = PCA9685Pin.PWM_02;
    Pin TRANSLATEUR = PCA9685Pin.PWM_01;

    // Constantes de position //
    // ---------------------- //
    int POS_FOURCHE_HAUT = 810;
    int POS_FOURCHE_BAS = 1800;

    int POS_BLOCAGE_DROITE_OUVERT = 2200;
    int POS_BLOCAGE_DROITE_FERME = 980;

    int POS_BLOCAGE_GAUCHE_OUVERT = 660;
    int POS_BLOCAGE_GAUCHE_FERME = 1830;

    int POS_TRANSLATEUR_GAUCHE = 2240;
    int POS_TRANSLATEUR_CENTRE = 1680;
    int POS_TRANSLATEUR_DROITE = 930;

}
