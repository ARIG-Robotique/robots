package org.arig.robot.constants;

/**
 * @author gdepuille on 21/12/14.
 */
public interface IConstantesServos {

    // Constantes de vitesse //
    // --------------------- //

    byte SPEED_SERRAGE_PALET = 0;
    byte SPEED_PIVOT_VENTOUSE = 0;
    byte SPEED_ASCENSEUR = 0;
    byte SPEED_PORTE_BARILLET = 0;
    byte SPEED_TRAPPE_MAGASIN = 0;
    byte SPEED_EJECTION_MAGASIN = 0;
    byte SPEED_POUSSE_ACCELERATEUR = 0;

    // Tempo servos //
    // ------------ //

    long WAIT_PINCE_SERRAGE_PALET = 500;
    long WAIT_PIVOT_VENTOUSE = 500;
    long WAIT_ASCENSEUR_VENTOUSE = 500;
    long WAIT_PORTE_BARILLET = 500;
    long WAIT_TRAPPE_MAGASIN = 500;
    long WAIT_EJECTION_MAGASIN = 500;
    long WAIT_POUSSE_ACCELERATEUR = 500;

    // Constantes d'identification Servo //
    // --------------------------------- //

    byte MOTOR_BARILLET = 1;
    byte MOTOR_DROIT = 2;
    byte MOTOR_GAUCHE = 3;
    byte POMPE_A_VIDE_DROIT = 18;
    byte POMPE_A_VIDE_GAUCHE = 19;

    byte PINCE_SERRAGE_PALET_DROIT = 4;
    byte PINCE_SERRAGE_PALET_GAUCHE = 5;
    byte PIVOT_VENTOUSE_DROIT = 6;
    byte PIVOT_VENTOUSE_GAUCHE = 7;
    byte ASCENSEUR_VENTOUSE_DROIT = 8;
    byte ASCENSEUR_VENTOUSE_GAUCHE = 9;
    byte OUVERTURE_PORTE_BARILLET_DROIT = 10;
    byte OUVERTURE_PORTE_BARILLET_GAUCHE = 11;
    byte OUVERTURE_TRAPPE_MAGASIN_DROIT = 12;
    byte OUVERTURE_TRAPPE_MAGASIN_GAUCHE = 13;
    byte EJECTION_MAGASIN_DROIT = 14;
    byte EJECTION_MAGASIN_GAUCHE = 15;
    byte POUSSE_ACCELERATEUR_DROIT = 16;
    byte POUSSE_ACCELERATEUR_GAUCHE = 17;

    // Constantes de position //
    // ---------------------- //

    int PINCE_SERRAGE_PALET_DROIT_OUVERT = 1500;
    int PINCE_SERRAGE_PALET_DROIT_INTER = 1500;
    int PINCE_SERRAGE_PALET_DROIT_FERME = 1500;

    int PINCE_SERRAGE_PALET_GAUCHE_OUVERT = 1500;
    int PINCE_SERRAGE_PALET_GAUCHE_INTER = 1500;
    int PINCE_SERRAGE_PALET_GAUCHE_FERME = 1500;

    int PIVOT_VENTOUSE_DROIT_MAGASIN = 1500;
    int PIVOT_VENTOUSE_DROIT_PRISE_FACADE = 1500;
    int PIVOT_VENTOUSE_DROIT_TABLE = 1500;

    int PIVOT_VENTOUSE_GAUCHE_MAGASIN = 1500;
    int PIVOT_VENTOUSE_GAUCHE_PRISE_FACADE = 1500;
    int PIVOT_VENTOUSE_GAUCHE_TABLE = 1500;

    int ASCENSEUR_DROIT_HAUT = 1500;
    int ASCENSEUR_DROIT_ACCELERATEUR = 1500;
    int ASCENSEUR_DROIT_BAS = 1500;

    int ASCENSEUR_GAUCHE_HAUT = 1500;
    int ASCENSEUR_GAUCHE_ACCELERATEUR = 1500;
    int ASCENSEUR_GAUCHE_BAS = 1500;

    int PORTE_BARILLET_DROIT_OUVERT = 1500;
    int PORTE_BARILLET_DROIT_FERME = 1500;

    int PORTE_BARILLET_GAUCHE_OUVERT = 1500;
    int PORTE_BARILLET_GAUCHE_FERME = 1500;

    int TRAPPE_MAGASIN_DROIT_OUVERT = 1500;
    int TRAPPE_MAGASIN_DROIT_FERME = 1500;

    int TRAPPE_MAGASIN_GAUCHE_OUVERT = 1500;
    int TRAPPE_MAGASIN_GAUCHE_FERME = 1500;

    int EJECTION_MAGASIN_DROIT_OUVERT = 1500;
    int EJECTION_MAGASIN_DROIT_FERME = 1500;

    int EJECTION_MAGASIN_GAUCHE_OUVERT = 1500;
    int EJECTION_MAGASIN_GAUCHE_FERME = 1500;

    int POUSSE_ACCELERATEUR_DROIT_FERME = 1500;
    int POUSSE_ACCELERATEUR_DROIT_STANDBY = 1500;
    int POUSSE_ACCELERATEUR_DROIT_ACTION = 1500;

    int POUSSE_ACCELERATEUR_GAUCHE_FERME = 1500;
    int POUSSE_ACCELERATEUR_GAUCHE_STANDBY = 1500;
    int POUSSE_ACCELERATEUR_GAUCHE_ACTION = 1500;

    // Constantes moteurs //
    // ------------------ //

    int MOTOR_ASPIRATION_STOP = 1000;
    int MOTOR_ASPIRATION_FULL = 2000;

    int MOTOR_REVERSE_FULL = 1000;
    int MOTOR_REVERSE_MEDIUM = 1250;
    int MOTOR_STOP = 1500;
    int MOTOR_FORWARD_MEDIUM = 1750;
    int MOTOR_FORWARD_FULL = 2000;
}
