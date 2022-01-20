package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OdinServosService extends AbstractServosService {

    // Constantes de vitesse //
    // --------------------- //

    private static byte SPEED_BRAS = 0;
    private static byte SPEED_PAVILLON = 0;
    private static byte SPEED_POUSSOIR = 0;
    private static byte SPEED_POUSSOIR_POUSSETTE = 20;

    // Tempo servos //
    // ------------ //

    private static int WAIT_BRAS = 440;
    private static int WAIT_PAVILLON = 300;
    private static int WAIT_POUSSOIR = 500; // TODO
    private static int WAIT_POUSSOIR_POUSSETTE = 500; // TODO

    // Constantes d'identification Servo //
    // --------------------------------- //
    private static byte BRAS_GAUCHE = 14;
    private static byte BRAS_DROIT = 15;
    private static byte PAVILLON = 21;
    private static byte POUSSOIR_AVANT_GAUCHE = 20;
    private static byte POUSSOIR_AVANT_DROIT = 19;
    private static byte POUSSOIR_ARRIERE_GAUCHE = 17;
    private static byte POUSSOIR_ARRIERE_DROIT = 18;

    // Constantes de position //
    // ---------------------- //

    private static int POS_BRAS_GAUCHE_FERME = 2050;
    private static int POS_BRAS_DROIT_FERME = 780;
    private static int POS_PAVILLON_BAS = 1400;
    private static int POS_POUSSOIR_AVANT_GAUCHE_BAS = 2410;
    private static int POS_POUSSOIR_AVANT_DROIT_BAS = 652;
    private static int POS_POUSSOIR_ARRIERE_GAUCHE_BAS = 710;
    private static int POS_POUSSOIR_ARRIERE_DROIT_BAS = 2140;

    private static final String SERVO_BRAS_GAUCHE = "Bras gauche";
    private static final String SERVO_BRAS_DROIT = "Bras droit";
    private static final String SERVO_PAVILLON = "Pavillon";
    private static final String SERVO_POUSSOIR_AVANT_GAUCHE = "Poussoir avant gauche";
    private static final String SERVO_POUSSOIR_AVANT_DROIT = "Poussoir avant droit";
    private static final String SERVO_POUSSOIR_ARRIERE_GAUCHE = "Poussoir arrière gauche";
    private static final String SERVO_POUSSOIR_ARRIERE_DROIT = "Poussoir arrière droit";

    private static final String SERVO1 = "Servo 1";
    private static final String SERVO2 = "Servo 2";

    private static final String POS_FERME = "Fermé";
    private static final String POS_BAS = "Bas";
    private static final String POS_OUVERT = "Ouvert";
    private static final String POS_POUSETTE = "Pousette";

    private static final String GROUP1 = "Groupe 1";

    public OdinServosService() {
        super();

        servo(BRAS_DROIT, SERVO_BRAS_DROIT).position(POS_FERME, POS_BRAS_DROIT_FERME);
        servo(BRAS_GAUCHE, SERVO_BRAS_GAUCHE).position(POS_FERME, POS_BRAS_GAUCHE_FERME);
        servo(PAVILLON, SERVO_PAVILLON).position(POS_BAS, POS_PAVILLON_BAS);
        servo(POUSSOIR_AVANT_GAUCHE, SERVO_POUSSOIR_AVANT_GAUCHE).position(POS_BAS, POS_POUSSOIR_AVANT_GAUCHE_BAS);
        servo(POUSSOIR_AVANT_DROIT, SERVO_POUSSOIR_AVANT_DROIT).position(POS_BAS, POS_POUSSOIR_AVANT_DROIT_BAS);
        servo(POUSSOIR_ARRIERE_GAUCHE, SERVO_POUSSOIR_ARRIERE_GAUCHE).position(POS_BAS, POS_POUSSOIR_ARRIERE_GAUCHE_BAS);
        servo(POUSSOIR_ARRIERE_DROIT, SERVO_POUSSOIR_ARRIERE_DROIT).position(POS_BAS, POS_POUSSOIR_ARRIERE_DROIT_BAS);

//        group(1, GROUP1)
//                .servo(
//                        servo(1, SERVO1)
//                                .time(500)
//                                .position(POS_POUSETTE, 1500, 20)
//                                .position(POS_OUVERT, 1000)
//                                .position(POS_FERME, 2000)
//                )
//                .servo(
//                        servo(2, SERVO2)
//                                .time(500)
//                                .position(POS_POUSETTE, 1500, 20)
//                                .position(POS_OUVERT, 2000)
//                                .position(POS_FERME, 1000)
//                )
//                .batch(POS_POUSETTE)
//                .batch(POS_OUVERT)
//                .batch(POS_FERME);
    }

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
       setPosition(SERVO_BRAS_DROIT, POS_FERME, false);
       setPosition(SERVO_BRAS_GAUCHE, POS_FERME, false);
       setPosition(SERVO_PAVILLON, POS_BAS, false);
       setPosition(SERVO_POUSSOIR_AVANT_GAUCHE, POS_BAS, false);
       setPosition(SERVO_POUSSOIR_AVANT_DROIT, POS_BAS, false);
       setPosition(SERVO_POUSSOIR_ARRIERE_GAUCHE, POS_BAS, false);
       setPosition(SERVO_POUSSOIR_ARRIERE_DROIT, POS_BAS, false);
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//

    public boolean isServo1Ouvert() {
        return isInPosition(SERVO1, POS_OUVERT);
    }

    //*******************************************//
    //* Déplacements                            *//
    //*******************************************//

    public void group1Ouvert(boolean wait) {
        setPositionBatch(GROUP1, POS_OUVERT, wait);
    }

    public void servo1Ouvert(boolean wait) {
        setPosition(SERVO1, POS_OUVERT, wait);
    }

}
