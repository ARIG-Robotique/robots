package org.arig.prehistobot.constants;

/**
 * Created by gdepuille on 21/12/14.
 */
public interface ConstantesServos {

    static final byte SPEED_BRAS = 25;
    static final byte SPEED_PORTE = 15;

    static final byte SERVO_BRAS_DROIT = 1;
    static final byte SERVO_BRAS_GAUCHE = 2;
    static final byte SERVO_PORTE_DROITE = 3;
    static final byte SERVO_PORTE_GAUCHE = 5;

    static final int BRAS_DROIT_HOME = 1865;
    static final int BRAS_DROIT_CDX_HAUT =	1040;
    static final int BRAS_DROIT_BOUG_HAUT = 1500; // TBD
    static final int BRAS_DROIT_BOUG_BAS = 1500; // TBD

    static final int BRAS_GAUCHE_HOME = 790;
    static final int BRAS_GAUCHE_CDX_HAUT = 1620;
    static final int BRAS_GAUCHE_BOUG_HAUT = 1500; // TBD
    static final int BRAS_GAUCHE_BOUG_BAS = 1500; // TBD

    static final int PORTE_DROITE_CLOSE = 850;
    static final int PORTE_DROITE_OPEN = 1860;
    static final int PORTE_DROITE_INTERM = 1500;

    static final int PORTE_GAUCHE_CLOSE = 1850;
    static final int PORTE_GAUCHE_OPEN = 950;
    static final int PORTE_GAUCHE_INTERM = 1310;
}
