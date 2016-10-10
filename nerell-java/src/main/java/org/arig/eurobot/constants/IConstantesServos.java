package org.arig.eurobot.constants;

/**
 * Created by gdepuille on 21/12/14.
 */
public interface IConstantesServos {

    // Constantes de vitesse
    static final byte SPEED_BRAS = 50;
    static final byte SPEED_TAPIS = 25;
    static final byte SPEED_GOBELET = 0;
    static final byte SPEED_MONTE_GOBELET = 0;
    static final byte SPEED_ASCENSEUR = 0;
    static final byte SPEED_PINCE = 0;
    static final byte SPEED_GUIDE = 0;

    // Tempo servos
    static final long WAIT_PINCE = 400;
    static final long WAIT_ASCENSEUR = 500;
    static final long WAIT_GUIDE = 500;
    static final long WAIT_MONTE_GB = 500;
    static final long WAIT_PRODUIT = 400;

    // Constantes d'identification Servo
    static final byte BRAS_DROIT = 15;
    static final byte BRAS_GAUCHE = 10;
    static final byte TAPIS_DROIT = 11;
    static final byte TAPIS_GAUCHE = 14;
    static final byte PRODUIT_DROIT = 12;
    static final byte PRODUIT_GAUCHE = 13;
    static final byte MONTE_GOBELET_DROIT = 20;
    static final byte MONTE_GOBELET_GAUCHE = 19;
    static final byte ASCENSEUR = 18;
    static final byte PINCE = 17;
    static final byte GUIDE = 21;

    // Constantes de position
    static final int BRAS_DROIT_HAUT = 1930;
    static final int BRAS_DROIT_CLAP = 1250;
    static final int BRAS_DROIT_BAS = 860;

    static final int BRAS_GAUCHE_HAUT = 700;
    static final int BRAS_GAUCHE_CLAP = 1450;
    static final int BRAS_GAUCHE_BAS = 1910;

    static final int TAPIS_DROIT_OUVERT = 1200;
    static final int TAPIS_DROIT_FERME = 2110;

    static final int TAPIS_GAUCHE_OUVERT = 1500;
    static final int TAPIS_GAUCHE_FERME = 670;

    static final int MONTE_GB_DROIT_HAUT = 1010;
    static final int MONTE_GB_DROIT_BAS = 2360;

    static final int MONTE_GB_GAUCHE_HAUT = 1790;
    static final int MONTE_GB_GAUCHE_BAS = 530;

    static final int PRODUIT_DROIT_OUVERT = 1400;
    static final int PRODUIT_DROIT_FERME = 1890;
    static final int PRODUIT_DROIT_INIT = 2290;

    static final int PRODUIT_GAUCHE_OUVERT = 1650;
    static final int PRODUIT_GAUCHE_FERME = 1160;
    static final int PRODUIT_GAUCHE_INIT = 750;

    static final int PINCE_OUVERTE = 1150;
    static final int PINCE_COULEUR = 1600;
    static final int PINCE_PRISE_BALLE = 1710;
    static final int PINCE_PRISE_PIED = 1935;

    static final int ASCENSEUR_HAUT_PIED = 2210;
    static final int ASCENSEUR_HAUT_BALLE = 1700;
    static final int ASCENSEUR_PLEIN = 970;
    static final int ASCENSEUR_DEPOSE_BORDURE = 1300;
    static final int ASCENSEUR_BAS = 790;

    static final int GUIDE_OUVERT = 1240;
    static final int GUIDE_FERME = 1470;
}
