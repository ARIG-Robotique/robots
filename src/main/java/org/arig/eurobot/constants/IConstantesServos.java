package org.arig.eurobot.constants;

/**
 * Created by gdepuille on 21/12/14.
 */
public interface IConstantesServos {

    // Constantes de vitesse
    static final byte SPEED_BRAS = 25;
    static final byte SPEED_TAPIS = 15;
    static final byte SPEED_GOBELET = 50;
    static final byte SPEED_MONTE_GOBELET = 30;
    static final byte SPEED_ASCENSEUR = 60;
    static final byte SPEED_PINCE = 60;
    static final byte SPEED_GUIDE = 15;
    static final byte SPEED_SONAR = 15;

    // Constantes d'identification Servo
    static final byte BRAS_DROIT = 15;
    static final byte BRAS_GAUCHE = 10;
    static final byte TAPIS_DROIT = 11;
    static final byte TAPIS_GAUCHE = 14;
    static final byte GOBELET_DROIT = 12;
    static final byte GOBELET_GAUCHE = 13;
    static final byte MONTE_GOBELET_DROIT = 20;
    static final byte MONTE_GOBELET_GAUCHE = 19;
    static final byte ASCENSEUR = 18;
    static final byte PINCE = 17;
    static final byte GUIDE = 21;
    static final byte SONAR = 7;

    // Constantes de position
    static final int SONAR_DROITE = 2040;
    static final int SONAR_CENTRE = 1660;
    static final int SONAR_GAUCHE = 1110;

    static final int BRAS_DROIT_HAUT = 1930;
    static final int BRAS_DROIT_BAS = 860;

    static final int BRAS_GAUCHE_HAUT = 700;
    static final int BRAS_GAUCHE_BAS = 1910;

    static final int TAPIS_DROIT_OUVERT = 1780;
    static final int TAPIS_DROIT_FERME = 2140;

    static final int TAPIS_GAUCHE_OUVERT = 960;
    static final int TAPIS_GAUCHE_FERME = 635;

    static final int MONTE_GB_DROIT_HAUT = 1010;
    static final int MONTE_GB_DROIT_BAS = 2360;

    static final int MONTE_GB_GAUCHE_HAUT = 1790;
    static final int MONTE_GB_GAUCHE_BAS = 530;

    static final int GOBELET_DROIT_OUVERT = 1040;
    static final int GOBELET_DROIT_PRODUIT = 1890;
    static final int GOBELET_DROIT_FERME = 2040;

    static final int GOBELET_GAUCHE_OUVERT = 2050;
    static final int GOBELET_GAUCHE_PRODUIT = 1160;
    static final int GOBELET_GAUCHE_FERME = 1010;

    static final int PINCE_OUVERTE = 1010;
    static final int PINCE_COULEUR = 1520;
    static final int PINCE_FERME = 1588;

    static final int ASCENSEUR_HAUT = 2210;
    static final int ASCENSEUR_BAS = 790;

    static final int GUIDE_OUVERT = 1240;
    static final int GUIDE_FERME = 1470;
}
