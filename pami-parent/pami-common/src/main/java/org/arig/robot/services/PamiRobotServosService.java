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
                .position(POS_INIT, 1910)
                .position(POS_OUVERT, 1600)
                .position(POS_OUVERT_JAUNE, 1600)
                .position(POS_OUVERT_BLEU, 1600)
                .position(POS_FERME, 1910);
            touchePlanteDroite = servo(TOUCHE_PLANTE_DROITE_ID, TOUCHE_PLANTE_DROITE)
                .time(500)
                .position(POS_INIT, 1010)
                .position(POS_OUVERT, 1810)
                .position(POS_OUVERT_JAUNE, 1810)
                .position(POS_OUVERT_BLEU, 1810)
                .position(POS_FERME, 1010);

        } else if (robotName.id() == RobotName.RobotIdentification.PAMI_CARRE) {
            touchePlanteGauche = servo(TOUCHE_PLANTE_GAUCHE_ID, TOUCHE_PLANTE_GAUCHE)
                .time(500)
                .position(POS_INIT, 1300)
                .position(POS_OUVERT, 600)
                .position(POS_OUVERT_JAUNE, 1501)
                .position(POS_OUVERT_BLEU, 1000)
                .position(POS_FERME, 1900);
            touchePlanteDroite = servo(TOUCHE_PLANTE_DROITE_ID, TOUCHE_PLANTE_DROITE)
                .time(500)
                .position(POS_INIT, 1700)
                .position(POS_OUVERT, 2150)
                .position(POS_OUVERT_JAUNE, 1900)
                .position(POS_OUVERT_BLEU, 1400)
                .position(POS_FERME, 960);

        } else {
            touchePlanteGauche = servo(TOUCHE_PLANTE_GAUCHE_ID, TOUCHE_PLANTE_GAUCHE)
                .time(500)
                .position(POS_INIT, 1501)
                .position(POS_OUVERT, 660)
                .position(POS_OUVERT_JAUNE, 950)
                .position(POS_OUVERT_BLEU, 660)
                .position(POS_FERME, 1960);
            touchePlanteDroite = servo(TOUCHE_PLANTE_DROITE_ID, TOUCHE_PLANTE_DROITE)
                .time(500)
                .position(POS_INIT, 1501)
                .position(POS_OUVERT, 2150)
                .position(POS_OUVERT_JAUNE, 2150)
                .position(POS_OUVERT_BLEU, 1950)
                .position(POS_FERME, 960);
        }

        group(GROUP_TOUCHE_PLANTE_ID, GROUP_TOUCHE_PLANTE)
            .addServo(touchePlanteGauche)
            .addServo(touchePlanteDroite)
            .batch(POS_INIT)
            .batch(POS_OUVERT)
            .batch(POS_OUVERT_JAUNE)
            .batch(POS_OUVERT_BLEU)
            .batch(POS_FERME);
    }
}
