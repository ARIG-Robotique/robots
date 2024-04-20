package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.servos.Servo;
import org.arig.robot.system.servos.AbstractServos;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PamiRobotServosService extends AbstractCommonPamiServosService {

    private static final byte TOUCHE_PLANTE_GAUCHE_ID = 1;
    private static final byte TOUCHE_PLANTE_DROITE_ID = 2;

    public PamiRobotServosService(AbstractServos pamiServos) {
        super(pamiServos);

        Servo touchePlanteGauche = servo(TOUCHE_PLANTE_GAUCHE_ID, TOUCHE_PLANTE_GAUCHE)
            .time(500)
            .position(POS_OUVERT, 1500)
            .position(POS_FERME, 1500);
        Servo touchePlanteDroite = servo(TOUCHE_PLANTE_DROITE_ID, TOUCHE_PLANTE_DROITE)
            .time(500)
            .position(POS_OUVERT, 1500)
            .position(POS_FERME, 1500);
        group(GROUP_TOUCHE_PLANTE_ID, GROUP_TOUCHE_PLANTE)
            .addServo(touchePlanteGauche)
            .addServo(touchePlanteDroite)
            .batch(POS_OUVERT)
            .batch(POS_FERME);
    }
}
