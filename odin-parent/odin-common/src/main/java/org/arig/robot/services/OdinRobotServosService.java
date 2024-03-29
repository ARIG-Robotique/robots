package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.servos.Servo;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.stereotype.Service;

import static org.arig.robot.model.servos.Servo.POS_0DEG;

@Slf4j
@Service
public class OdinRobotServosService extends AbstractCommonRobotServosService {
    public OdinRobotServosService(SD21Servos sd21Servos) {
        super(sd21Servos);
    }
}
