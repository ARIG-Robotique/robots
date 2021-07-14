package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NerellServosService extends AbstractServosService {

    private static final String SERVO1 = "Servo 1";
    private static final String SERVO2 = "Servo 2";

    private static final String POS_FERME = "Fermé";
    private static final String POS_OUVERT = "Ouvert";
    private static final String POS_POUSETTE = "Pousette";

    private static final String GROUP1 = "Groupe 1";

    public NerellServosService() {
        super();

        group(1, GROUP1)
                .servo(
                        servo(1, SERVO1)
                                .time(500)
                                .position(POS_POUSETTE, 1500, 20)
                                .position(POS_OUVERT, 1000)
                                .position(POS_FERME, 2000)
                )
                .servo(
                        servo(2, SERVO2)
                                .time(500)
                                .position(POS_POUSETTE, 1500, 20)
                                .position(POS_OUVERT, 2000)
                                .position(POS_FERME, 1000)
                )
                .batch(POS_POUSETTE)
                .batch(POS_OUVERT)
                .batch(POS_FERME);
    }

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
        setPosition(SERVO1, POS_FERME, false);
        setPosition(SERVO2, POS_FERME, false);
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
