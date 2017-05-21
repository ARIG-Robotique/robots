package org.arig.robot.constants;

/**
 * @author gdepuille on 21/12/14.
 */
public interface IConstantesServos {

    // Constantes de vitesse
    byte SPEED_PINCE = 0;
    byte SPEED_INC_BRAS = 0;
    byte SPEED_ROT_VENTOUSE = 0;
    byte SPEED_PORTE_MAG = 0;
    byte SPEED_BLOCAGE_MAG = 0;
    byte SPEED_DEVIDOIR = 0;
    byte SPEED_INC_ASPI = 0;

    // Tempo servos
    long WAIT_PINCE = 400;
    long WAIT_INC_BRAS = 400;
    long WAIT_ROT_VENTOUSE = 400;
    long WAIT_PORTE_MAG = 200;
    long WAIT_BLOCAGE_MAG = 200;
    long WAIT_DEVIDOIR = 400;
    long WAIT_INC_ASPI = 400;

    // Constantes d'identification Servo
    byte MOTOR_ROULEAUX = 1;
    byte MOTOR_EJECTION = 3;
    byte MOTOR_DROIT = 2;
    byte MOTOR_GAUCHE = 4;
    byte MOTOR_ASPIRATION = 16;

    byte PINCE_MODULE_DROIT = 7;
    byte PINCE_MODULE_CENTRE = 6;
    byte INCLINAISON_BRAS = 13;
    byte ROTATION_VENTOUSE = 12;
    byte INCLINAISON_ASPIRATION = 11;
    byte DEVIDOIR = 18;
    byte BLOCAGE_ENTREE_MAG = 19;
    byte PORTE_MAGASIN_DROIT = 20;
    byte PORTE_MAGASIN_GAUCHE = 21;

    // Constantes de position
    // int XXX_POS_1
    // int XXX_POS_2

    int PINCE_MODULE_DROIT_OUVERT = 1870;
    int PINCE_MODULE_DROIT_PRISE_PRODUIT = 1775;
    int PINCE_MODULE_DROIT_CHARGEMENT_VENTOUSE = 1295;
    int PINCE_MODULE_DROIT_FERME = 795;

    int PINCE_MODULE_CENTRE_OUVERT_DANS_DROIT = 1860;
    int PINCE_MODULE_CENTRE_OUVERT = 1635;
    int PINCE_MODULE_CENTRE_FERME = 490;

    int ROTATION_VENTOUSE_PRISE_ROBOT = 640;
    int ROTATION_VENTOUSE_PRISE_FUSEE = 2160;
    int ROTATION_VENTOUSE_DEPOSE_MAGASIN = 1390;

    int INCLINAISON_BRAS_PRISE_ROBOT = 1150;
    int INCLINAISON_BRAS_PRISE_FUSEE = 640;
    int INCLINAISON_BRAS_ATTENTE = 1010;
    int INCLINAISON_BRAS_DEPOSE = 2350;
    int INCLINAISON_BRAS_VERTICAL = 750;

    int PORTE_DROITE_OUVERT = 830;
    int PORTE_DROITE_FERME = 2020;

    int PORTE_GAUCHE_OUVERT = 1800;
    int PORTE_GAUCHE_FERME = 830;

    int BLOCAGE_OUVERT = 650;
    int BLOCAGE_FERME = 1500;

    int INCLINAISON_ASPI_FERME = 500;
    int INCLINAISON_ASPI_OUVERT = 2400;

    int DEVIDOIR_CHARGEMENT = 950;
    int DEVIDOIR_DECHARGEMENT = 1680;
    int DEVIDOIR_LECTURE_COULEUR = 1350;
}
