package org.arig.robot.constants;

/**
 * @author gdepuille on 21/12/14.
 */
public interface IConstantesServos {

    // Constantes de vitesse
    byte SPEED_BRAS = 50;
    byte SPEED_TAPIS = 25;
    byte SPEED_GOBELET = 0;
    byte SPEED_MONTE_GOBELET = 0;
    byte SPEED_ASCENSEUR = 0;
    byte SPEED_PINCE = 0;
    byte SPEED_GUIDE = 0;

    // Tempo servos
    long WAIT_PINCE = 400;
    long WAIT_ASCENSEUR = 500;
    long WAIT_GUIDE = 500;
    long WAIT_MONTE_GB = 500;
    long WAIT_PRODUIT = 400;

    // Constantes d'identification Servo
    byte MOTOR_1 = 4;
    byte MOTOR_2 = 5;
    byte BRAS_DROIT = 15;
    byte BRAS_GAUCHE = 10;
    byte TAPIS_DROIT = 11;
    byte TAPIS_GAUCHE = 14;
    byte PRODUIT_DROIT = 12;
    byte PRODUIT_GAUCHE = 13;
    byte MONTE_GOBELET_DROIT = 20;
    byte MONTE_GOBELET_GAUCHE = 19;
    byte ASCENSEUR = 18;
    byte PINCE = 17;
    byte GUIDE = 21;

    // Constantes de position
    int BRAS_DROIT_HAUT = 1930;
    int BRAS_DROIT_CLAP = 1250;
    int BRAS_DROIT_BAS = 860;

    int BRAS_GAUCHE_HAUT = 700;
    int BRAS_GAUCHE_CLAP = 1450;
    int BRAS_GAUCHE_BAS = 1910;

    int TAPIS_DROIT_OUVERT = 1200;
    int TAPIS_DROIT_FERME = 2110;

    int TAPIS_GAUCHE_OUVERT = 1500;
    int TAPIS_GAUCHE_FERME = 670;

    int MONTE_GB_DROIT_HAUT = 1010;
    int MONTE_GB_DROIT_BAS = 2360;

    int MONTE_GB_GAUCHE_HAUT = 1790;
    int MONTE_GB_GAUCHE_BAS = 530;

    int PRODUIT_DROIT_OUVERT = 1400;
    int PRODUIT_DROIT_FERME = 1890;
    int PRODUIT_DROIT_INIT = 2290;

    int PRODUIT_GAUCHE_OUVERT = 1650;
    int PRODUIT_GAUCHE_FERME = 1160;
    int PRODUIT_GAUCHE_INIT = 750;

    int PINCE_OUVERTE = 1150;
    int PINCE_COULEUR = 1600;
    int PINCE_PRISE_BALLE = 1710;
    int PINCE_PRISE_PIED = 1935;

    int ASCENSEUR_HAUT_PIED = 2210;
    int ASCENSEUR_HAUT_BALLE = 1700;
    int ASCENSEUR_PLEIN = 970;
    int ASCENSEUR_DEPOSE_BORDURE = 1300;
    int ASCENSEUR_BAS = 790;

    int GUIDE_OUVERT = 1240;
    int GUIDE_FERME = 1470;
}
