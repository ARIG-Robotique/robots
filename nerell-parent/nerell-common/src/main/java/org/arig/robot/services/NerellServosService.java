package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.INerellConstantesServos;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NerellServosService extends AbstractServosService {

    @Autowired
    private SD21Servos servos;

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
        servos.setPositionAndSpeed(INerellConstantesServos.SERVO1, INerellConstantesServos.POS_SERVO1_FERME, INerellConstantesServos.SPEED_SERVO1);
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//

    public boolean isServo1Ouvert() {
        return servos.getPosition(INerellConstantesServos.SERVO1) == INerellConstantesServos.POS_SERVO1_OUVERT;
    }

    //*******************************************//
    //* Déplacements                            *//
    //*******************************************//

    public void servosOuvert(boolean wait) {
        logPositionServo("Servos", "Ouvert", wait);
        setPositionBatchAndSpeed(
                INerellConstantesServos.BATCH1,
                INerellConstantesServos.POS_BATCH1_OUVERT,
                INerellConstantesServos.SPEED_SERVO1,
                wait);
    }

    public void servo1Ouvert(boolean wait) {
        logPositionServo("Servo 1", "Ouvert", wait);
        setPosition(INerellConstantesServos.SERVO1, INerellConstantesServos.POS_SERVO1_OUVERT, wait);
    }

}
