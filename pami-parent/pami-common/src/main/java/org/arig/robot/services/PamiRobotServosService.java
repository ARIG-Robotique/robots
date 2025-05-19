package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.RobotName;
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
        .position(POS_FERME, 600)
        .position(POS_OUVERT_1, 2400)
        .position(POS_OUVERT_2, 1700);

    } else if (robotName.id() == RobotName.RobotIdentification.PAMI_CARRE) {
      servo(HAND_ID, HAND)
        .time(500)
        .position(POS_FERME, 600)
        .position(POS_OUVERT_1, 2400)
        .position(POS_OUVERT_2, 1800);

    } else if (robotName.id() == RobotName.RobotIdentification.PAMI_ROND) {
      servo(HAND_ID, HAND)
        .time(500)
        .position(POS_FERME, 600)
        .position(POS_OUVERT_1, 2400)
        .position(POS_OUVERT_2, 1700);

    } else {
      servo(HAND_ID, HAND)
        .time(500)
        .position(POS_FERME, 1500)
        .position(POS_OUVERT_1, 1600)
        .position(POS_OUVERT_2, 1400);
    }

    group(1, "Servos").addServo(servo(HAND));
  }
}
