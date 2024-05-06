package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.servos.Servo;
import org.arig.robot.system.servos.AbstractServos;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PamiRobotServosService extends AbstractCommonPamiServosService {

    private static final byte TOUCHE_PLANTE_GAUCHE_ID = 1;
    private static final byte TOUCHE_PLANTE_DROITE_ID = 2;

    public PamiRobotServosService(RobotName robotName, AbstractServos pamiServos) {
        super(pamiServos);

        final Servo touchePlanteGauche;
        final Servo touchePlanteDroite;
        if (robotName.id() == RobotName.RobotIdentification.PAMI_TRIANGLE) {
            touchePlanteGauche = servo(TOUCHE_PLANTE_GAUCHE_ID, TOUCHE_PLANTE_GAUCHE)
                .time(500)
                .position(POS_OUVERT_BLEU, 1500)
                .position(POS_OUVERT_JAUNE, 1600)
                .position(POS_FERME, 1910);
            touchePlanteDroite = servo(TOUCHE_PLANTE_DROITE_ID, TOUCHE_PLANTE_DROITE)
                .time(500)
                .position(POS_OUVERT_BLEU, 1500)
                .position(POS_OUVERT_JAUNE, 1810)
                .position(POS_FERME, 1010);
        } else if (robotName.id() == RobotName.RobotIdentification.PAMI_CARRE) {
            touchePlanteGauche = servo(TOUCHE_PLANTE_GAUCHE_ID, TOUCHE_PLANTE_GAUCHE)
                .time(500)
                .position(POS_OUVERT_BLEU, 1500)
                .position(POS_OUVERT_JAUNE, 1500)
                .position(POS_FERME, 1500);
            touchePlanteDroite = servo(TOUCHE_PLANTE_DROITE_ID, TOUCHE_PLANTE_DROITE)
                .time(500)
                .position(POS_OUVERT_BLEU, 1500)
                .position(POS_OUVERT_JAUNE, 1500)
                .position(POS_FERME, 1500);
        } else {
            touchePlanteGauche = servo(TOUCHE_PLANTE_GAUCHE_ID, TOUCHE_PLANTE_GAUCHE)
                .time(500)
                .position(POS_OUVERT_BLEU, 1500)
                .position(POS_OUVERT_JAUNE, 1500)
                .position(POS_FERME, 1500);
            touchePlanteDroite = servo(TOUCHE_PLANTE_DROITE_ID, TOUCHE_PLANTE_DROITE)
                .time(500)
                .position(POS_OUVERT_BLEU, 1500)
                .position(POS_OUVERT_JAUNE, 1500)
                .position(POS_FERME, 1500);
        }

        group(GROUP_TOUCHE_PLANTE_ID, GROUP_TOUCHE_PLANTE)
            .addServo(touchePlanteGauche)
            .addServo(touchePlanteDroite)
            .batch(POS_OUVERT_BLEU)
            .batch(POS_OUVERT_JAUNE)
            .batch(POS_FERME);
    }
}
