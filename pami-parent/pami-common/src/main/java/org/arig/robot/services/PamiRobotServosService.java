package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.servos.Servo;
import org.arig.robot.system.servos.AbstractServos;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PamiRobotServosService extends AbstractCommonPamiServosService {

    private static final byte HAND_ID = 1;

    public PamiRobotServosService(RobotName robotName, AbstractServos pamiServos) {
        super(pamiServos);

        if (robotName.id() == RobotName.RobotIdentification.PAMI_TRIANGLE) {
            servo(HAND_ID, HAND)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_OUVERT_1, 1600)
                .position(POS_OUVERT_2, 1400);

        } else if (robotName.id() == RobotName.RobotIdentification.PAMI_CARRE) {
            servo(HAND_ID, HAND)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_OUVERT_1, 1600)
                .position(POS_OUVERT_2, 1400);

        } else {
            servo(HAND_ID, HAND)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_OUVERT_1, 1600)
                .position(POS_OUVERT_2, 1400);
        }
    }
}
