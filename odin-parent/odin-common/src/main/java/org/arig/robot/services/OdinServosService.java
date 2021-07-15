package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IOdinConstantesServos;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OdinServosService extends AbstractServosService {

    @Autowired
    private SD21Servos servos;

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
        servos.setPositionAndSpeed(IOdinConstantesServos.SERVO1, IOdinConstantesServos.POS_SERVO1_FERME, IOdinConstantesServos.SPEED_SERVO1);
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//


    //*******************************************//
    //* Déplacements                            *//
    //*******************************************//

    public void servo1Ferme(boolean wait) {
        logPositionServo("Servo 1", "Fermé", wait);
        setPosition(IOdinConstantesServos.SERVO1, IOdinConstantesServos.POS_SERVO1_FERME, wait);
    }

}
